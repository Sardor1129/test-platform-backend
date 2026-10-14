package uz.testplatform.dto;

import jakarta.validation.constraints.NotBlank;

public record OptionRequest(
        @NotBlank(message = "Variant matni kiritilmagan") String text,
        boolean correct
) {}