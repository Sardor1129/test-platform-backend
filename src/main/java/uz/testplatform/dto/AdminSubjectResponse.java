package uz.testplatform.dto;

public record AdminSubjectResponse(
        Long id,
        String nameUz,
        String nameRu,
        String nameEn,
        Integer durationMinutes,
        Integer questionCount
) {}