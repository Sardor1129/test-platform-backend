package uz.testplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.testplatform.entity.User;
import uz.testplatform.exception.ApiException;
import uz.testplatform.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvatarService {

    // Ruxsat etilgan turlar va ularga mos kengaytma
    private static final Map<String, String> TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

    private final UserRepository userRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Transactional
    public String upload(String login, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Fayl tanlanmagan");
        }
        String extension = TYPES.get(file.getContentType());
        if (extension == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Faqat JPG, PNG yoki WEBP rasm yuklash mumkin");
        }

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Foydalanuvchi topilmadi"));

        try {
            Path dir = Paths.get(uploadDir, "avatars").toAbsolutePath();
            Files.createDirectories(dir);

            // Fayl nomini o'zimiz yaratamiz, foydalanuvchi yuborgan nomga ishonmaymiz
            String filename = UUID.randomUUID() + extension;
            file.transferTo(dir.resolve(filename));

            user.setAvatarUrl("/uploads/avatars/" + filename);
            userRepository.save(user);
            return user.getAvatarUrl();
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Rasmni saqlab bo'lmadi");
        }
    }
}