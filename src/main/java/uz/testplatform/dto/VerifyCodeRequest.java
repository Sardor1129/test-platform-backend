package uz.testplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyCodeRequest(
        @NotBlank(message = "Email kiritilmagan")
        @Email(message = "Email noto'g'ri") String email,
        @NotBlank(message = "Kod kiritilmagan") String code
) {}