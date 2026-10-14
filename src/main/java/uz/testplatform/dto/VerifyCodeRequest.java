package uz.testplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyCodeRequest(
        @Pattern(regexp = "^\\+998\\d{9}$", message = "Telefon noto'g'ri") String phone,
        @NotBlank(message = "Kod kiritilmagan") String code
) {}