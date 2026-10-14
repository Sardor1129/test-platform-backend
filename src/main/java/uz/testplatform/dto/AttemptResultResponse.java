package uz.testplatform.dto;

public record AttemptResultResponse(
        Long attemptId,
        int totalQuestions,
        int correctCount,
        int wrongCount,
        double percent,
        boolean passed,
        boolean certificateAvailable
) {}