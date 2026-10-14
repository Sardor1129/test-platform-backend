package uz.testplatform.dto;

public record AuthResponse(
        String token,
        String role,
        String firstName,
        String lastName
) {}
