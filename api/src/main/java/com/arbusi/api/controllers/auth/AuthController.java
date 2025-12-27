package com.arbusi.api.controllers.auth;

import com.arbusi.api.controllers.auth.dto.AuthRequestDto;
import com.arbusi.api.controllers.auth.dto.AuthResponseDto;
import com.arbusi.api.controllers.auth.dto.MeResponseDto;
import com.arbusi.api.controllers.auth.dto.PasswordResetRequestDto;
import com.arbusi.api.controllers.auth.dto.SendPasswordResetEmailRequestDto;
import com.arbusi.api.controllers.auth.services.AuthControllerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthControllerService authControllerService;

    public AuthController(
            AuthControllerService authControllerService
    ) {
        this.authControllerService = authControllerService;
    }

    @Operation(
            summary = "Login",
            description = "Authenticates the user, generates a JWT token"
    )
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto req, HttpServletResponse resp) {
        return ResponseEntity.ok(authControllerService.login(req, resp));
    }

    @Operation(
            summary = "Register",
            description = "Creates new user entity in database. Authenticates the user, generates a JWT token"
    )
    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody AuthRequestDto req, HttpServletResponse resp) {
        return new ResponseEntity<>(authControllerService.register(req, resp), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Refresh",
            description = "Called in case of expired JWT token. Generates new JWT token"
    )
    @PostMapping(value = "/refresh", produces = "application/json")
    public ResponseEntity<AuthResponseDto> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse resp) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(authControllerService.refreshToken(refreshToken, resp));
    }

    // Delete refresh token. Frontend should forget JWT token.
    @Operation(
            summary = "Logout",
            description = "Deletes the refresh token. Logs out user form all instances."
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse resp) {
        authControllerService.logout(resp);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Me",
            description = "Returns user info"
    )
    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<MeResponseDto> me() {
        return ResponseEntity.ok(authControllerService.me());
    }

    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf() {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordResetRequestDto requestDto, HttpServletResponse resp) {
        authControllerService.resetPassword(requestDto, resp);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<Void> sendResetPasswordEmail(@RequestBody @Valid SendPasswordResetEmailRequestDto requestDto) {
        authControllerService.sendResetPasswordEmail(requestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}