package uz.testplatform.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AttemptResponse(
        Long id,
        Long subjectId,
        String subjectName,
        String language,
        LocalDateTime startedAt,
        LocalDateTime deadline,
        long remainingSeconds,
        List<AttemptQuestionResponse> questions
) {}