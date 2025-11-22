package com.arbusi.api.services.impl;

import com.arbusi.api.controllers.dto.AuthResponseDto;
import com.arbusi.api.enums.TokenType;
import com.arbusi.api.exceptions.NotFoundException;
import com.arbusi.api.models.User;
import com.arbusi.api.properties.TokenProperties;
import com.arbusi.api.security.jwt.JwtUtils;
import com.arbusi.api.services.OAuth2Service;
import com.arbusi.api.services.TokenService;
import com.arbusi.api.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class OAuth2ServiceImpl implements OAuth2Service {
    private final UserService userService;
    private final TokenService tokenService;
    private final JwtUtils jwtUtils;
    private final TokenProperties tokenProperties;

    public OAuth2ServiceImpl(
            UserService userService,
            TokenService tokenService,
            JwtUtils jwtUtils,
            TokenProperties tokenProperties
    ) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.jwtUtils = jwtUtils;
        this.tokenProperties = tokenProperties;
    }

    @Override
    public AuthResponseDto loginOauth2(Authentication authentication, HttpServletResponse resp) {
        String email = ((OidcUser) authentication.getPrincipal()).getEmail();

        User user = userService.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        String jwtToken = jwtUtils.generateToken(email);
        String refreshToken = tokenService.createToken(user, TokenType.JWT_REFRESH).getToken();

        Duration maxAge = Duration.ofSeconds(tokenProperties.jwtRefresh().validDurationSeconds());

        // Remove unused JSESSIONID cookie (Maybe there is a better way to do it but idk)
        // Oauth2 automatically generates JSESSIONID cookie
        Cookie jsessionCookie = new Cookie("JSESSIONID", null);
        jsessionCookie.setPath("/");
        jsessionCookie.setMaxAge(0);
        resp.addCookie(jsessionCookie);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/v1/auth/refresh")
                .maxAge(maxAge)
                .build();
        resp.addHeader("Set-Cookie", cookie.toString());

        return new AuthResponseDto(jwtToken);
    }
}
