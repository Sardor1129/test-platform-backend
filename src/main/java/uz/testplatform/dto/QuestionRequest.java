package uz.testplatform.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record QuestionRequest(
        @NotNull(message = "Fan tanlanmagan") Long subjectId,
        @NotBlank(message = "Til tanlanmagan")
        @Pattern(regexp = "^(uz|ru|en)$", message = "Til uz, ru yoki en bo'lsin") String language,
        @NotBlank(message = "Savol matni kiritilmagan") String text,
        String imageUrl,
        @NotNull(message = "Variantlar kiritilmagan")
        @Size(min = 4, max = 4, message = "Aynan 4 ta variant bo'lishi kerak")
        @Valid List<OptionRequest> options
) {}