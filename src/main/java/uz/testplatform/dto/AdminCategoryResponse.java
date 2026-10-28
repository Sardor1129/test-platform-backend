package uz.testplatform.dto;

public record AdminCategoryResponse(
        Long id,
        String nameUz,
        String nameRu,
        String nameEn,
        long subjectCount
) {}