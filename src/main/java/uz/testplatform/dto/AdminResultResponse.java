package uz.testplatform.dto;

import java.time.LocalDateTime;

public record AdminResultResponse(
        Long attemptId,
        String fullName,
        String email,
        String subjectName,
        String language,
        String attemptType,
        int correctCount,
        int totalQuestions,
        double percent,
        boolean passed,
        LocalDateTime finishedAt
) {}