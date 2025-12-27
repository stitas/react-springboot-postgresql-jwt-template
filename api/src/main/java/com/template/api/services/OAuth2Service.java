package com.template.api.services;

import com.template.api.controllers.auth.dto.AuthResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface OAuth2Service {
    AuthResponseDto loginOauth2(Authentication authentication, HttpServletResponse resp);
}
