package uz.testplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record StartAttemptRequest(
        @NotNull(message = "Fan tanlanmagan") Long subjectId,
        @NotBlank(message = "Til tanlanmagan")
        @Pattern(regexp = "^(uz|ru|en)$", message = "Til uz, ru yoki en bo'lsin") String language
) {}