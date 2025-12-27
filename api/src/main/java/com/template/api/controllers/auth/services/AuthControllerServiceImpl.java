package com.template.api.controllers.auth.services;

import com.template.api.controllers.auth.dto.AuthRequestDto;
import com.template.api.controllers.auth.dto.AuthResponseDto;
import com.template.api.controllers.auth.dto.MeResponseDto;
import com.template.api.controllers.auth.dto.PasswordResetRequestDto;
import com.template.api.controllers.auth.dto.SendPasswordResetEmailRequestDto;
import com.template.api.enums.AuthSource;
import com.template.api.enums.TokenType;
import com.template.api.enums.UserRole;
import com.template.api.exceptions.ConflictException;
import com.template.api.exceptions.ForbiddenException;
import com.template.api.exceptions.NotAllowedException;
import com.template.api.exceptions.NotFoundException;
import com.template.api.models.Token;
import com.template.api.models.User;
import com.template.api.properties.TokenProperties;
import com.template.api.security.SecurityUser;
import com.template.api.security.jwt.JwtUtils;
import com.template.api.services.TokenService;
import com.template.api.services.UserService;
import com.template.api.services.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthControllerServiceImpl implements AuthControllerService {
    private static final String REFRESH_ENDPOINT = "/api/v1/auth/refresh";

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;
    private final MailService mailService;
    private final JwtUtils jwtUtils;
    private final TokenProperties tokenProperties;

    public AuthControllerServiceImpl(
            AuthenticationManager authenticationManager,
            UserService userService,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            TokenService tokenService,
            MailService mailService,
            JwtUtils jwtUtils,
            TokenProperties tokenProperties
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenService = tokenService;
        this.mailService = mailService;
        this.jwtUtils = jwtUtils;
        this.tokenProperties = tokenProperties;
    }

    @Override
    public AuthResponseDto register(AuthRequestDto req, HttpServletResponse resp) {
        if(userService.existsByEmail(req.email())) {
            throw new ConflictException("User with such email exists");
        }

        User user = new User();
        user.setEmail(req.email());
        user.setPasswordHash(bCryptPasswordEncoder.encode(req.password()));
        user.setRole(UserRole.FREE);
        user.setAuthSource(AuthSource.LOCAL);
        userService.save(user);

        return login(new AuthRequestDto(req.email(), req.password()), resp);
    }

    @Override
    public AuthResponseDto login(AuthRequestDto req, HttpServletResponse resp) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        SecurityUser securityUser = (SecurityUser) auth.getPrincipal();
        User user = securityUser.user();

        String jwtToken = jwtUtils.generateToken(user.getEmail());
        String refreshToken = tokenService.createToken(user, TokenType.JWT_REFRESH).getToken();

        writeRefreshCookie(resp, refreshToken, false);

        return new AuthResponseDto(jwtToken);
    }

    @Override
    public AuthResponseDto refreshToken(String refreshToken, HttpServletResponse resp) {
        Token newRefreshToken = tokenService.rotateRefreshToken(refreshToken);
        String newJwtToken = jwtUtils.generateToken(newRefreshToken.getUser().getEmail());

        writeRefreshCookie(resp, newRefreshToken.getToken(), false);

        return new AuthResponseDto(newJwtToken);
    }

    @Override
    public void logout(HttpServletResponse resp) {
        User user = userService.getCurrentUser();
        // Log user out of all instances
        tokenService.deleteByUserAndType(user, TokenType.JWT_REFRESH);
        writeRefreshCookie(resp, "", true);
    }

    @Override
    public MeResponseDto me() {
        User user = userService.getCurrentUser();
        return new MeResponseDto(user.getId(), user.getEmail(), user.getRole());
    }

    @Override
    public void resetPassword(PasswordResetRequestDto requestDto, HttpServletResponse resp) {
        Token token = tokenService.findByToken(requestDto.token()).orElseThrow(
                () -> new NotFoundException("Token was not found")
        );

        if (token.getExpireAt().isBefore(LocalDateTime.now()) || token.getUsed()) {
            throw new ForbiddenException("Token expired");
        }

        token.setUsed(true);
        tokenService.save(token);

        User user = token.getUser();
        user.setPasswordHash(bCryptPasswordEncoder.encode(requestDto.password()));
        userService.save(user);

        logout(resp);
    }

    @Override
    public void sendResetPasswordEmail(SendPasswordResetEmailRequestDto requestDto) {
        User user = userService.findByEmail(requestDto.email()).orElseThrow(
                () -> new NotFoundException("Unknown User")
        );

        if (user.getAuthSource() != AuthSource.LOCAL) {
            throw new NotAllowedException("Oauth users can't change their password");
        }

        Token token = tokenService.createToken(user, TokenType.PASSWORD_RESET);
        mailService.sendPasswordResetEmail(user, token.getToken());
    }

    private void writeRefreshCookie(HttpServletResponse resp, String refreshToken, boolean clear) {
        Duration maxAge = clear ? Duration.ofMillis(0) : Duration.ofSeconds(tokenProperties.jwtRefresh().validDurationSeconds());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path(REFRESH_ENDPOINT)
                .maxAge(maxAge)
                .build();
        resp.addHeader("Set-Cookie", cookie.toString());
    }
}