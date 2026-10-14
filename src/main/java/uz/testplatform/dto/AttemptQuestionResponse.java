package uz.testplatform.dto;

import java.util.List;

public record AttemptQuestionResponse(
        Integer orderNumber,
        Long questionId,
        String text,
        String imageUrl,
        List<AttemptOptionResponse> options,
        Long selectedOptionId
) {}