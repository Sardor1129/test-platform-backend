package uz.testplatform.dto;

public record ProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        String email,
        String phone,
        String login,
        String avatarUrl
) {}
