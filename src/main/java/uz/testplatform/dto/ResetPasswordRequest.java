package uz.testplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Email kiritilmagan")
        @Email(message = "Email noto'g'ri") String email,
        @NotBlank(message = "Kod kiritilmagan") String code,
        @NotBlank(message = "Yangi parol kiritilmagan")
        @Size(min = 6, message = "Parol kamida 6 belgi bo'lsin") String newPassword,
        @NotBlank(message = "Parolni takrorlang") String confirmPassword
) {}