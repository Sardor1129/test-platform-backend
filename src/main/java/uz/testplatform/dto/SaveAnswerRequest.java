package uz.testplatform.dto;

import jakarta.validation.constraints.NotNull;

public record SaveAnswerRequest(
        @NotNull(message = "Savol ko'rsatilmagan") Long questionId,
        Long optionId
) {}