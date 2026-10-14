package uz.testplatform.dto;

import jakarta.validation.constraints.Pattern;

public record PhoneRequest(
        @Pattern(regexp = "^\\+998\\d{9}$", message = "Telefon noto'g'ri") String phone
) {}
