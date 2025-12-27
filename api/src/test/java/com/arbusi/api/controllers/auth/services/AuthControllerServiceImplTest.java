package com.arbusi.api.controllers.auth.services;

import com.arbusi.api.controllers.auth.dto.AuthRequestDto;
import com.arbusi.api.controllers.auth.dto.AuthResponseDto;
import com.arbusi.api.controllers.auth.dto.MeResponseDto;
import com.arbusi.api.controllers.auth.dto.PasswordResetRequestDto;
import com.arbusi.api.controllers.auth.dto.SendPasswordResetEmailRequestDto;
import com.arbusi.api.enums.AuthSource;
import com.arbusi.api.enums.TokenType;
import com.arbusi.api.enums.UserRole;
import com.arbusi.api.exceptions.ConflictException;
import com.arbusi.api.exceptions.ForbiddenException;
import com.arbusi.api.exceptions.NotAllowedException;
import com.arbusi.api.exceptions.NotFoundException;
import com.arbusi.api.models.Token;
import com.arbusi.api.models.User;
import com.arbusi.api.properties.TokenProperties;
import com.arbusi.api.security.SecurityUser;
import com.arbusi.api.security.jwt.JwtUtils;
import com.arbusi.api.services.TokenService;
import com.arbusi.api.services.UserService;
import com.arbusi.api.services.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerServiceImplTest {
    private static final String EMAIL = "email@example.com";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String JWT = "jwt-token";
    private static final String JWT2 = "jwt-token-2";
    private static final String REFRESH = "refresh-token";
    private static final String NEW_REFRESH = "new-refresh-token";
    private static final String REFRESH_PATH = "/api/v1/auth/refresh";
    private static final String NEW_PASSWORD = "newStrongPass!1";
    private static final String ENCODED_NEW_PASSWORD = "encodedNew";
    private static final String RESET_TOKEN = "reset-token";

    private AuthControllerServiceImpl service;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private MailService mailService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TokenProperties tokenProperties;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Token> tokenCaptor;

    @BeforeEach
    void setUp() {
        service = new AuthControllerServiceImpl(
                authenticationManager,
                userService,
                bCryptPasswordEncoder,
                tokenService,
                mailService,
                jwtUtils,
                tokenProperties
        );
    }

    @Test
    void whenRegister_andEmailExists_thenThrowConflict() {
        when(userService.existsByEmail(EMAIL)).thenReturn(true);

        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertThrows(ConflictException.class, () -> service.register(new AuthRequestDto(EMAIL, PASSWORD), resp));
    }

    @Test
    void whenRegister_thenEncodeSaveAuthenticateIssueJwtAndRefreshCookie() {
        when(userService.existsByEmail(EMAIL)).thenReturn(false);
        when(bCryptPasswordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);

        User persisted = user(EMAIL, ENCODED_PASSWORD);
        when(userService.save(any(User.class))).thenReturn(persisted);

        SecurityUser principal = new SecurityUser(persisted);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtUtils.generateToken(EMAIL)).thenReturn(JWT);

        Token refresh = token(
                REFRESH,
                persisted,
                TokenType.JWT_REFRESH,
                LocalDateTime.now().plusHours(1),
                false
        );
        when(tokenService.createToken(persisted, TokenType.JWT_REFRESH)).thenReturn(refresh);

        MockHttpServletResponse resp = new MockHttpServletResponse();

        AuthResponseDto dto = service.register(new AuthRequestDto(EMAIL, PASSWORD), resp);

        verify(bCryptPasswordEncoder).encode(PASSWORD);
        verify(userService).save(userCaptor.capture());
        verify(authenticationManager).authenticate(any());
        verify(jwtUtils).generateToken(EMAIL);
        verify(tokenService).createToken(persisted, TokenType.JWT_REFRESH);

        assertEquals(EMAIL, userCaptor.getValue().getEmail());
        assertEquals(JWT, dto.token());

        String setCookie = resp.getHeader("Set-Cookie");
        assertNotNull(setCookie);
        assertTrue(setCookie.contains("refresh_token=" + REFRESH));
        assertTrue(setCookie.contains("Path=" + REFRESH_PATH));
        assertTrue(setCookie.contains("HttpOnly"));
        assertTrue(setCookie.contains("Secure"));
        assertTrue(setCookie.contains("SameSite=None"));
    }

    @Test
    void whenLogin_thenAuthenticateLoadUserIssueJwtAndRefreshCookie() {
        User persisted = user(EMAIL, ENCODED_PASSWORD);

        SecurityUser principal = new SecurityUser(persisted);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtUtils.generateToken(EMAIL)).thenReturn(JWT);

        Token refresh = token(
                REFRESH,
                persisted,
                TokenType.JWT_REFRESH,
                LocalDateTime.now().plusHours(1),
                false
        );
        when(tokenService.createToken(persisted, TokenType.JWT_REFRESH)).thenReturn(refresh);

        MockHttpServletResponse resp = new MockHttpServletResponse();

        AuthResponseDto dto = service.login(new AuthRequestDto(EMAIL, PASSWORD), resp);

        verify(authenticationManager).authenticate(any());
        verify(jwtUtils).generateToken(EMAIL);
        verify(tokenService).createToken(persisted, TokenType.JWT_REFRESH);
        verifyNoInteractions(userService);

        assertEquals(JWT, dto.token());

        String setCookie = resp.getHeader("Set-Cookie");
        assertNotNull(setCookie);
        assertTrue(setCookie.contains("refresh_token=" + REFRESH));
        assertTrue(setCookie.contains("Path=" + REFRESH_PATH));
    }

    @Test
    void whenLogin_userNotFound_thenThrowNotFound() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new NotFoundException("User not found"));

        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertThrows(NotFoundException.class,
                () -> service.login(new AuthRequestDto(EMAIL, PASSWORD), resp));

        verify(authenticationManager).authenticate(any());
        verifyNoInteractions(userService, jwtUtils, tokenService);
    }


    @Test
    void whenRefreshToken_thenRotateIssueNewJwtAndCookie() {
        User persisted = user(EMAIL, ENCODED_PASSWORD);
        Token rotated = token(NEW_REFRESH, persisted, TokenType.JWT_REFRESH, LocalDateTime.now().plusHours(2), false);
        when(tokenService.rotateRefreshToken(REFRESH)).thenReturn(rotated);
        when(jwtUtils.generateToken(EMAIL)).thenReturn(JWT2);

        MockHttpServletResponse resp = new MockHttpServletResponse();

        AuthResponseDto dto = service.refreshToken(REFRESH, resp);

        verify(tokenService).rotateRefreshToken(REFRESH);
        verify(jwtUtils).generateToken(EMAIL);
        assertEquals(JWT2, dto.token());

        String setCookie = resp.getHeader("Set-Cookie");
        assertTrue(setCookie.contains("refresh_token=" + NEW_REFRESH));
        assertTrue(setCookie.contains("Path=" + REFRESH_PATH));
    }

    @Test
    void whenLogout_thenDeleteByUserAndClearCookie() {
        User persisted = user(EMAIL, ENCODED_PASSWORD);
        when(userService.getCurrentUser()).thenReturn(persisted);

        MockHttpServletResponse resp = new MockHttpServletResponse();

        service.logout(resp);

        verify(tokenService).deleteByUserAndType(persisted, TokenType.JWT_REFRESH);

        String setCookie = resp.getHeader("Set-Cookie");
        assertTrue(setCookie.contains("refresh_token="));
        assertTrue(setCookie.contains("Max-Age=0"));
        assertTrue(setCookie.contains("Path=" + REFRESH_PATH));
    }

    @Test
    void whenMe_thenReturnDto() {
        User persisted = user(EMAIL, ENCODED_PASSWORD);
        when(userService.getCurrentUser()).thenReturn(persisted);

        MeResponseDto dto = service.me();

        assertEquals(EMAIL, dto.email());
        assertEquals(UserRole.FREE, dto.role());
    }

    @Test
    void whenResetPassword_unknownToken_thenThrowNotFound() {
        when(tokenService.findByToken(RESET_TOKEN)).thenReturn(Optional.empty());
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertThrows(NotFoundException.class, () -> service.resetPassword(new PasswordResetRequestDto(RESET_TOKEN, NEW_PASSWORD), resp));
    }

    @Test
    void whenResetPassword_usedToken_thenThrowForbidden() {
        User persisted = user(EMAIL, ENCODED_PASSWORD);
        Token used = token(RESET_TOKEN, persisted, TokenType.PASSWORD_RESET, LocalDateTime.now().plusMinutes(15), true);
        when(tokenService.findByToken(RESET_TOKEN)).thenReturn(Optional.of(used));
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertThrows(ForbiddenException.class, () -> service.resetPassword(new PasswordResetRequestDto(RESET_TOKEN, NEW_PASSWORD), resp));
    }

    @Test
    void whenResetPassword_expiredToken_thenThrowForbidden() {
        User persisted = user(EMAIL, ENCODED_PASSWORD);
        Token expired = token(RESET_TOKEN, persisted, TokenType.PASSWORD_RESET, LocalDateTime.now().minusSeconds(1), false);
        when(tokenService.findByToken(RESET_TOKEN)).thenReturn(Optional.of(expired));
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertThrows(ForbiddenException.class, () -> service.resetPassword(new PasswordResetRequestDto(RESET_TOKEN, NEW_PASSWORD), resp));
    }

    @Test
    void whenResetPassword_valid_thenMarkUsedSavePasswordLogoutAndClearCookie() {
        User persisted = user(EMAIL, ENCODED_PASSWORD);
        Token ok = token(RESET_TOKEN, persisted, TokenType.PASSWORD_RESET, LocalDateTime.now().plusMinutes(20), false);
        when(tokenService.findByToken(RESET_TOKEN)).thenReturn(Optional.of(ok));
        when(bCryptPasswordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);
        when(tokenService.save(any(Token.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userService.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userService.getCurrentUser()).thenReturn(persisted);

        MockHttpServletResponse resp = new MockHttpServletResponse();

        service.resetPassword(new PasswordResetRequestDto(RESET_TOKEN, NEW_PASSWORD), resp);

        verify(tokenService).save(tokenCaptor.capture());
        verify(bCryptPasswordEncoder).encode(NEW_PASSWORD);
        verify(userService).save(userCaptor.capture());
        verify(tokenService).deleteByUserAndType(persisted, TokenType.JWT_REFRESH);

        Token savedToken = tokenCaptor.getValue();
        User savedUser = userCaptor.getValue();

        assertTrue(savedToken.getUsed());
        assertEquals(ENCODED_NEW_PASSWORD, savedUser.getPasswordHash());

        String setCookie = resp.getHeader("Set-Cookie");
        assertTrue(setCookie.contains("refresh_token="));
        assertTrue(setCookie.contains("Max-Age=0"));
        assertTrue(setCookie.contains("Path=" + REFRESH_PATH));
    }

    @Test
    void whenSendResetPasswordEmail_unknownUser_thenThrowNotFound() {
        when(userService.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.sendResetPasswordEmail(new SendPasswordResetEmailRequestDto(EMAIL)));
    }

    @Test
    void whenSendResetPasswordEmail_andUserNotLocal_thenThrowNotAllowed() {
        User user = user(EMAIL, ENCODED_PASSWORD);
        user.setAuthSource(AuthSource.GOOGLE);
        when(userService.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThrows(NotAllowedException.class, () -> service.sendResetPasswordEmail(new SendPasswordResetEmailRequestDto(EMAIL)));

        verify(tokenService, never()).createToken(any(), any());
        verify(mailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    void whenSendResetPasswordEmail_valid_thenCreateTokenAndSendEmail() {
        User persisted = user(EMAIL, ENCODED_PASSWORD);
        persisted.setAuthSource(AuthSource.LOCAL);
        when(userService.findByEmail(EMAIL)).thenReturn(Optional.of(persisted));
        Token t = token("reset-123", persisted, TokenType.PASSWORD_RESET, LocalDateTime.now().plusMinutes(15), false);
        when(tokenService.createToken(persisted, TokenType.PASSWORD_RESET)).thenReturn(t);

        service.sendResetPasswordEmail(new SendPasswordResetEmailRequestDto(EMAIL));

        verify(tokenService).createToken(persisted, TokenType.PASSWORD_RESET);
        verify(mailService).sendPasswordResetEmail(persisted, "reset-123");
    }

    private User user(String email, String passwordHash) {
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(passwordHash);
        u.setRole(UserRole.FREE);
        return u;
    }

    private Token token(String value, User user, TokenType type, LocalDateTime expireAt, boolean used) {
        Token t = new Token();
        t.setToken(value);
        t.setUser(user);
        t.setType(type);
        t.setExpireAt(expireAt);
        t.setUsed(used);
        return t;
    }
}