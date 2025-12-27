package com.template.api.controllers.auth.dto;

import com.template.api.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing information about the authenticated user")
public record MeResponseDto(
        @Schema(description = "Unique identifier of the user", example = "123")
        Long id,

        @Schema(description = "User's email address", example = "user@example.com")
        String email,

        @Schema(description = "Role assigned to the user", example = "FREE")
        UserRole role
) { }
