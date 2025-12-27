package com.arbusi.api.services;

import com.arbusi.api.controllers.auth.dto.AuthResponseDto;
import com.arbusi.api.enums.AuthSource;
import com.arbusi.api.enums.TokenType;
import com.arbusi.api.enums.UserRole;
import com.arbusi.api.exceptions.NotFoundException;
import com.arbusi.api.models.Token;
import com.arbusi.api.models.User;
import com.arbusi.api.properties.TokenProperties;
import com.arbusi.api.security.jwt.JwtUtils;
import com.arbusi.api.services.impl.OAuth2ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OAuth2ServiceImplTest {
    private static final String EMAIL = "user@example.com";
    private static final String JWT_TOKEN = "jwt-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final int REFRESH_VALID_SECONDS = 3600;

    private OAuth2Service oAuth2Service;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private TokenProperties tokenProperties;

    @Mock
    private Authentication authentication;

    @Mock
    private OidcUser oidcUser;

    @Mock
    private HttpServletResponse httpServletResponse;

    @BeforeEach
    void setUp() {
        oAuth2Service = new OAuth2ServiceImpl(userService, tokenService, jwtUtils, tokenProperties);
    }

    @Test
    void whenLoginOauth2_validUser_thenReturnAuthResponseAndSetCookie() {
        User user = User.builder()
                .email(EMAIL)
                .authSource(AuthSource.LOCAL)
                .role(UserRole.FREE)
                .build();
        Token refreshToken = Token.builder()
                .token(REFRESH_TOKEN)
                .user(user)
                .type(TokenType.JWT_REFRESH)
                .used(false)
                .build();
        TokenProperties.TokenData refreshTokenProperties = new TokenProperties.TokenData(REFRESH_VALID_SECONDS);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(EMAIL);
        when(userService.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(EMAIL)).thenReturn(JWT_TOKEN);
        when(tokenService.createToken(user, TokenType.JWT_REFRESH)).thenReturn(refreshToken);
        when(tokenProperties.jwtRefresh()).thenReturn(refreshTokenProperties);

        AuthResponseDto response = oAuth2Service.loginOauth2(authentication, httpServletResponse);

        assertEquals(JWT_TOKEN, response.token());

        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpServletResponse).addHeader(eq("Set-Cookie"), headerValueCaptor.capture());
        String cookieValue = headerValueCaptor.getValue();

        assertTrue(cookieValue.contains("refresh_token=" + REFRESH_TOKEN));
        assertTrue(cookieValue.contains("Path=/api/v1/auth/refresh"));
        assertTrue(cookieValue.contains("Max-Age=" + REFRESH_VALID_SECONDS));
        assertTrue(cookieValue.contains("HttpOnly"));
        assertTrue(cookieValue.contains("Secure"));
        assertTrue(cookieValue.contains("SameSite=None"));

        verify(userService).findByEmail(EMAIL);
        verify(tokenService).createToken(user, TokenType.JWT_REFRESH);
        verify(jwtUtils).generateToken(EMAIL);
    }

    @Test
    void whenLoginOauth2_userNotFound_thenThrowNotFound() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(EMAIL);
        when(userService.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> oAuth2Service.loginOauth2(authentication, httpServletResponse));

        verify(httpServletResponse, never()).addHeader(anyString(), anyString());
        verify(tokenService, never()).createToken(any(), any());
        verify(jwtUtils, never()).generateToken(anyString());
    }
}
