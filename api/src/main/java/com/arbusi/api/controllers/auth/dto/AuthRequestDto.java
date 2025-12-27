package com.arbusi.api.controllers.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for user auth")
public record AuthRequestDto(
        @Schema(description = "User's email address", example = "user@example.com")
        @Email
        String email,

        @Schema(description = "User's plaintext password", example = "labas123")
        @NotBlank
        String password
) {
}
