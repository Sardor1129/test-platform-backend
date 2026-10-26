package uz.testplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.config.JwtService;
import uz.testplatform.dto.*;
import uz.testplatform.entity.CodePurpose;
import uz.testplatform.entity.Role;
import uz.testplatform.entity.User;
import uz.testplatform.entity.VerificationCode;
import uz.testplatform.exception.ApiException;
import uz.testplatform.repository.UserRepository;
import uz.testplatform.repository.VerificationCodeRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int CODE_LIFETIME_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;
    private final SecureRandom random = new SecureRandom();

    private final UserRepository userRepository;
    private final VerificationCodeRepository codeRepository;
    private final PendingRegistrationStore pendingStore;
    private final MailService mailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // ---------- LOGIN ----------

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.login(), request.password()));
        } catch (AuthenticationException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Login yoki parol noto'g'ri");
        }

        User user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Login yoki parol noto'g'ri"));

        return toResponse(user);
    }

    // ---------- RO'YXATDAN O'TISH ----------

    @Transactional
    public void sendRegisterCode(RegisterRequest request) {
        String email = normalize(request.email());

        if (!request.password().equals(request.confirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Parollar bir xil emas");
        }
        if (userRepository.existsByLogin(request.login())) {
            throw new ApiException(HttpStatus.CONFLICT, "Bu login band");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Bu email ro'yxatdan o'tgan");
        }

        pendingStore.put(email, request);
        issueCode(email, CodePurpose.REGISTER);
    }

    @Transactional
    public void resendRegisterCode(String rawEmail) {
        String email = normalize(rawEmail);
        if (pendingStore.get(email) == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Avval ma'lumotlarni to'ldiring");
        }
        issueCode(email, CodePurpose.REGISTER);
    }

    @Transactional(noRollbackFor = ApiException.class)
    public AuthResponse confirmRegister(VerifyCodeRequest request) {
        String email = normalize(request.email());

        RegisterRequest data = pendingStore.get(email);
        if (data == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Avval ma'lumotlarni to'ldiring");
        }

        verifyCode(email, CodePurpose.REGISTER, request.code());

        User user = User.builder()
                .firstName(data.firstName())
                .email(email)
                .login(data.login())
                .passwordHash(passwordEncoder.encode(data.password()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        pendingStore.remove(email);
        return toResponse(user);
    }

    // ---------- PAROL TIKLASH ----------

    @Transactional
    public void sendResetCode(String rawEmail) {
        String email = normalize(rawEmail);
        // Email ro'yxatda bo'lmasa ham xato bermaymiz (kim ro'yxatdan o'tganini bildirmaslik uchun)
        if (userRepository.existsByEmailIgnoreCase(email)) {
            issueCode(email, CodePurpose.RESET_PASSWORD);
        }
    }

    @Transactional(noRollbackFor = ApiException.class)
    public void checkResetCode(VerifyCodeRequest request) {
        checkCode(normalize(request.email()), CodePurpose.RESET_PASSWORD, request.code());
    }

    @Transactional(noRollbackFor = ApiException.class)
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Parollar bir xil emas");
        }

        String email = normalize(request.email());
        VerificationCode saved = checkCode(email, CodePurpose.RESET_PASSWORD, request.code());

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Foydalanuvchi topilmadi"));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        saved.setUsed(true);
        codeRepository.save(saved);
    }

    // ---------- YORDAMCHI METODLAR ----------

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private void issueCode(String email, CodePurpose purpose) {
        // Bir emailga 1 daqiqada ko'pi bilan bitta kod
        codeRepository.findFirstByEmailAndPurposeAndUsedFalseOrderByIdDesc(email, purpose)
                .ifPresent(last -> {
                    LocalDateTime sentAt = last.getExpiresAt().minusMinutes(CODE_LIFETIME_MINUTES);
                    if (sentAt.isAfter(LocalDateTime.now().minusSeconds(60))) {
                        throw new ApiException(HttpStatus.TOO_MANY_REQUESTS,
                                "Kodni qayta yuborish uchun 1 daqiqa kuting");
                    }
                });

        String code = String.format("%06d", random.nextInt(1_000_000));

        VerificationCode entity = VerificationCode.builder()
                .email(email)
                .code(code)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(CODE_LIFETIME_MINUTES))
                .used(false)
                .build();
        codeRepository.save(entity);

        mailService.sendCode(email, code);
    }

    // Kodni tekshiradi, lekin "ishlatilgan" deb belgilamaydi
    private VerificationCode checkCode(String email, CodePurpose purpose, String code) {
        VerificationCode saved = codeRepository
                .findFirstByEmailAndPurposeAndUsedFalseOrderByIdDesc(email, purpose)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Kod topilmadi, qayta yuboring"));

        if (saved.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Kod muddati tugagan, qayta yuboring");
        }
        if (!saved.getCode().equals(code)) {
            saved.setAttempts(saved.getAttempts() + 1);
            if (saved.getAttempts() >= MAX_ATTEMPTS) {
                saved.setUsed(true);                // kod bekor qilinadi
            }
            codeRepository.save(saved);
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    saved.isUsed() ? "Urinishlar tugadi, yangi kod so'rang" : "Kod noto'g'ri");
        }
        return saved;
    }

    // Kodni tekshiradi va ishlatilgan deb belgilaydi
    private void verifyCode(String email, CodePurpose purpose, String code) {
        VerificationCode saved = checkCode(email, purpose, code);
        saved.setUsed(true);
        codeRepository.save(saved);
    }

    private AuthResponse toResponse(User user) {
        String token = jwtService.generateToken(user.getLogin(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getFirstName());
    }
}