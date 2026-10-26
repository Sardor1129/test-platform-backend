package uz.testplatform.dto;

public record ProfileResponse(
        Long id,
        String firstName,
        String email,
        String login,
        String avatarUrl
) {}
