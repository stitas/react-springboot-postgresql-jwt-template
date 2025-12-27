package com.arbusi.api.controllers.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "Request body for sending password reset email")
public record SendPasswordResetEmailRequestDto(
        @Schema(description = "User's email address", example = "user@example.com")
        @Email
        String email
) {}
