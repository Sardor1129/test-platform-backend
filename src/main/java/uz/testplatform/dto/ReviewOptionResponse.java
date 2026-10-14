package uz.testplatform.dto;

public record ReviewOptionResponse(
        Long id,
        String label,
        String text,
        boolean correct,      // bu variant to'g'ri javobmi
        boolean selected      // foydalanuvchi shuni tanlaganmi
) {}