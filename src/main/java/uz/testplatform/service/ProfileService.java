package uz.testplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.config.JwtService;
import uz.testplatform.dto.AuthResponse;
import uz.testplatform.dto.ProfileResponse;
import uz.testplatform.dto.ProfileUpdateRequest;
import uz.testplatform.entity.User;
import uz.testplatform.exception.ApiException;
import uz.testplatform.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public ProfileResponse get(String login) {
        return toResponse(findUser(login));
    }

    @Transactional
    public AuthResponse update(String login, ProfileUpdateRequest request) {
        User user = findUser(login);

        if (!user.getLogin().equals(request.login())
                && userRepository.existsByLogin(request.login())) {
            throw new ApiException(HttpStatus.CONFLICT, "Bu login band");
        }
        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Bu email band");
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setMiddleName(request.middleName());
        user.setEmail(request.email());
        user.setLogin(request.login());
        userRepository.save(user);

        // Token ichida eski login bor, shuning uchun yangi token beramiz
        String token = jwtService.generateToken(user.getLogin(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getFirstName(), user.getLastName());
    }

    private User findUser(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Foydalanuvchi topilmadi"));
    }

    private ProfileResponse toResponse(User u) {
        return new ProfileResponse(u.getId(), u.getFirstName(), u.getLastName(), u.getMiddleName(),
                u.getEmail(), u.getPhone(), u.getLogin(), u.getAvatarUrl());
    }
}