package uz.testplatform.dto;

import java.time.LocalDateTime;

public record AttemptSummaryResponse(
        Long id,
        LocalDateTime date,
        String subjectName,
        String attemptType,        // "FIRST" yoki "RETAKE"
        double percent,
        boolean passed,
        boolean certificateAvailable
) {}