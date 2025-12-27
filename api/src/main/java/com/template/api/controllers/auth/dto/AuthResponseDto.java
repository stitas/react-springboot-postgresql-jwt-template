package com.template.api.controllers.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing JWT authentication token")
public record AuthResponseDto (
        @Schema(description = "JWT access token to be used for authenticating API requests", example = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJlQGUuY29tIiwiaWF0IjoxNzUyMDcyNTMzLCJleHAiOjE3NTIwNzM0MzN9.lNsYnd7OoaTMFb6wt6ql2Tu-5t44OB6pEdY1Swg3cOnWeynfV0MGpu1kgXXRw8a6")
        String token
) {}
