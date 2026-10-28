package uz.testplatform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubjectRequest(
        @NotBlank(message = "Fan nomi (uz) kiritilmagan") String nameUz,
        @NotBlank(message = "Fan nomi (ru) kiritilmagan") String nameRu,
        @NotBlank(message = "Fan nomi (en) kiritilmagan") String nameEn,
        @NotNull(message = "Test vaqti kiritilmagan")
        @Min(value = 1, message = "Test vaqti kamida 1 daqiqa bo'lsin") Integer durationMinutes,
        @NotNull(message = "Savollar soni kiritilmagan")
        @Min(value = 1, message = "Savollar soni kamida 1 bo'lsin") Integer questionCount,
        @NotNull(message = "O'tish foizi kiritilmagan")
        @Min(value = 1, message = "O'tish foizi kamida 1 bo'lsin")
        @Max(value = 100, message = "O'tish foizi 100 dan oshmasin") Integer passPercent,
        Long categoryId
) {}