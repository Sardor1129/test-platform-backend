package uz.testplatform.dto;

public record AdminOptionResponse(
        Long id,
        String label,
        String text,
        boolean correct
) {}