package uz.testplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.testplatform.entity.Avatar;
import uz.testplatform.entity.User;
import uz.testplatform.exception.ApiException;
import uz.testplatform.repository.AvatarRepository;
import uz.testplatform.repository.UserRepository;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private static final Set<String> TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final UserRepository userRepository;
    private final AvatarRepository avatarRepository;

    @Transactional
    public String upload(String login, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Fayl tanlanmagan");
        }
        if (file.getContentType() == null || !TYPES.contains(file.getContentType())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Faqat JPG, PNG yoki WEBP rasm yuklash mumkin");
        }

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Foydalanuvchi topilmadi"));

        try {
            // eski rasmni o'chiramiz, yangisini saqlaymiz
            avatarRepository.deleteByUserId(user.getId());

            Avatar avatar = Avatar.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();
            avatarRepository.save(avatar);

            user.setAvatarUrl("/api/avatars/" + avatar.getId());
            userRepository.save(user);
            return user.getAvatarUrl();
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Rasmni saqlab bo'lmadi");
        }
    }
}