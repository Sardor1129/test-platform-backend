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
    private final SmsService smsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


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


    @Transactional
    public void sendRegisterCode(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Parollar bir xil emas");
        }
        if (userRepository.existsByLogin(request.login())) {
            throw new ApiException(HttpStatus.CONFLICT, "Bu login band");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Bu email ro'yxatdan o'tgan");
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new ApiException(HttpStatus.CONFLICT, "Bu telefon raqam ro'yxatdan o'tgan");
        }
        pendingStore.put(request.phone(), request);
        issueCode(request.phone(), CodePurpose.REGISTER);
    }

    @Transactional
    public void resendRegisterCode(String phone) {
        if (pendingStore.get(phone) == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Avval ma'lumotlarni to'ldiring");
        }
        issueCode(phone, CodePurpose.REGISTER);
    }

    @Transactional(noRollbackFor = ApiException.class)
    public AuthResponse confirmRegister(VerifyCodeRequest request) {
        RegisterRequest data = pendingStore.get(request.phone());
        if (data == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Avval ma'lumotlarni to'ldiring");
        }

        verifyCode(request.phone(), CodePurpose.REGISTER, request.code());

        User user = User.builder()
                .firstName(data.firstName())
                .lastName(data.lastName())
                .middleName(data.middleName())
                .email(data.email())
                .phone(data.phone())
                .login(data.login())
                .passwordHash(passwordEncoder.encode(data.password()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        pendingStore.remove(request.phone());
        return toResponse(user);
    }

    @Transactional
    public void sendResetCode(String phone) {
        if (userRepository.existsByPhone(phone)) {
            issueCode(phone, CodePurpose.RESET_PASSWORD);
        }
    }

    @Transactional(noRollbackFor = ApiException.class)
    public void checkResetCode(VerifyCodeRequest request) {
        checkCode(request.phone(), CodePurpose.RESET_PASSWORD, request.code());
    }

    @Transactional(noRollbackFor = ApiException.class)
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Parollar bir xil emas");
        }

        VerificationCode saved = checkCode(
                request.phone(), CodePurpose.RESET_PASSWORD, request.code());

        User user = userRepository.findByPhone(request.phone())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Foydalanuvchi topilmadi"));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        saved.setUsed(true);
        codeRepository.save(saved);
    }


    private void issueCode(String phone, CodePurpose purpose) {
        String code = String.format("%06d", random.nextInt(1_000_000));

        VerificationCode entity = VerificationCode.builder()
                .phone(phone)
                .code(code)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(CODE_LIFETIME_MINUTES))
                .used(false)
                .build();
        codeRepository.save(entity);

        smsService.send(phone, "Tasdiqlash kodi: " + code);
    }

    private VerificationCode checkCode(String phone, CodePurpose purpose, String code) {
        VerificationCode saved = codeRepository
                .findFirstByPhoneAndPurposeAndUsedFalseOrderByIdDesc(phone, purpose)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Kod topilmadi, qayta yuboring"));

        if (saved.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Kod muddati tugagan, qayta yuboring");
        }
        if (!saved.getCode().equals(code)) {
            saved.setAttempts(saved.getAttempts() + 1);
            if (saved.getAttempts() >= MAX_ATTEMPTS) {
                saved.setUsed(true);              // kod bekor qilinadi
            }
            codeRepository.save(saved);
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    saved.isUsed() ? "Urinishlar tugadi, yangi kod so'rang" : "Kod noto'g'ri");
        }
        return saved;
    }

    private void verifyCode(String phone, CodePurpose purpose, String code) {
        VerificationCode saved = checkCode(phone, purpose, code);
        saved.setUsed(true);
        codeRepository.save(saved);
    }

    private AuthResponse toResponse(User user) {
        String token = jwtService.generateToken(user.getLogin(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getFirstName(), user.getLastName());
    }
}