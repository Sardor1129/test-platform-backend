package uz.testplatform.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "Kategoriya nomi (uz) kiritilmagan") String nameUz,
        @NotBlank(message = "Kategoriya nomi (ru) kiritilmagan") String nameRu,
        @NotBlank(message = "Kategoriya nomi (en) kiritilmagan") String nameEn
) {}