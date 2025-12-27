package com.arbusi.api.controllers.auth.services;

import com.arbusi.api.controllers.auth.dto.AuthRequestDto;
import com.arbusi.api.controllers.auth.dto.AuthResponseDto;
import com.arbusi.api.controllers.auth.dto.MeResponseDto;
import com.arbusi.api.controllers.auth.dto.PasswordResetRequestDto;
import com.arbusi.api.controllers.auth.dto.SendPasswordResetEmailRequestDto;
import jakarta.servlet.http.HttpServletResponse;

import java.security.Principal;

public interface AuthControllerService {
    AuthResponseDto register(AuthRequestDto req, HttpServletResponse resp);

    AuthResponseDto login(AuthRequestDto req, HttpServletResponse resp);

    AuthResponseDto refreshToken(String refreshToken, HttpServletResponse resp);

    void logout(Principal principal, HttpServletResponse resp);

    MeResponseDto me(Principal principal);

    void resetPassword(PasswordResetRequestDto requestDto, HttpServletResponse resp);

    void sendResetPasswordEmail(SendPasswordResetEmailRequestDto requestDto);
}
