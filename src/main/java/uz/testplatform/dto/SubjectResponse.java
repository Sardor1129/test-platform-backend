package uz.testplatform.dto;

public record SubjectResponse(
        Long id,
        String name,
        Integer durationMinutes,
        Integer questionCount
) {}