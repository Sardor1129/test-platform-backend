package uz.testplatform.dto;

import java.util.List;

public record ReviewResponse(
        Long attemptId,
        String subjectName,
        double percent,
        boolean passed,
        List<ReviewQuestionResponse> questions
) {}