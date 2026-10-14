package uz.testplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @Pattern(regexp = "^\\+998\\d{9}$", message = "Telefon noto'g'ri") String phone,
        @NotBlank(message = "Kod kiritilmagan") String code,
        @NotBlank(message = "Yangi parol kiritilmagan")
        @Size(min = 6, message = "Parol kamida 6 belgi bo'lsin") String newPassword,
        @NotBlank(message = "Parolni takrorlang") String confirmPassword
) {}