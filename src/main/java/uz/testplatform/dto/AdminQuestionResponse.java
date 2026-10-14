package uz.testplatform.dto;

import java.util.List;

public record AdminQuestionResponse(
        Long id,
        Long subjectId,
        String language,
        String text,
        String imageUrl,
        List<AdminOptionResponse> options
) {}