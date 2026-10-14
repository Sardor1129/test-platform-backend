package uz.testplatform.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message ="Login kiritilmagan") String login,
        @NotBlank(message = "Parol kiritilmagan") String password
) {}
