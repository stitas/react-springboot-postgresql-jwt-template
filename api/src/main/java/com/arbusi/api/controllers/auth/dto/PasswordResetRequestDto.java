package com.arbusi.api.controllers.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for user password reset")
public record PasswordResetRequestDto(
        @Schema(description = "Password reset token", example = "e0ec2a4a-498b-4fdb-8801-ac321e7s566f5")
        @NotBlank
        String token,

        @Schema(description = "User's new password", example = "12345")
        @NotBlank
        String password
) {}
