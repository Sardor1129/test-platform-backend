package uz.testplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank(message = "Ism kiritilmagan") String firstName,
        @NotBlank(message = "Familiya kiritilmagan") String lastName,
        String middleName,
        @NotBlank(message = "Email kiritilmagan")
        @Email(message = "Email noto'g'ri") String email,
        @NotBlank(message = "Login kiritilmagan")
        @Size(min = 4, message = "Login kamida 4 belgi bo'lsin") String login
) {}