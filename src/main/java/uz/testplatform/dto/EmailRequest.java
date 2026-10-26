package uz.testplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank(message = "Email kiritilmagan")
        @Email(message = "Email noto'g'ri") String email
) {}