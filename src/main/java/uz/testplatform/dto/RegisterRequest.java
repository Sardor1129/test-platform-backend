package uz.testplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Ism kiritilmagan") String firstName,
        @NotBlank(message = "Familiya kiritilmagan") String lastName,
        String middleName,
        @NotBlank(message = "Email kiritilmagan")
        @Email(message = "Email noto'g'ri") String email,
        @Pattern(regexp = "^\\+998\\d{9}$", message = "Telefon +998901234567 ko'rinishida bo'lsin") String phone,
        @NotBlank(message = "Login kiritilmagan")
        @Size(min = 4, message = "Login kamida 4 belgi bo'lsin") String login,
        @NotBlank(message = "Parol kiritilmagan")
        @Size(min = 6, message = "Parol kamida 6 belgi bo'lsin") String password,
        @NotBlank(message = "Parolni takrorlang") String confirmPassword
) {}
