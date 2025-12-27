package com.template.api.controllers.auth.services;

import com.template.api.controllers.auth.dto.AuthRequestDto;
import com.template.api.controllers.auth.dto.AuthResponseDto;
import com.template.api.controllers.auth.dto.MeResponseDto;
import com.template.api.controllers.auth.dto.PasswordResetRequestDto;
import com.template.api.controllers.auth.dto.SendPasswordResetEmailRequestDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthControllerService {
    AuthResponseDto register(AuthRequestDto req, HttpServletResponse resp);

    AuthResponseDto login(AuthRequestDto req, HttpServletResponse resp);

    AuthResponseDto refreshToken(String refreshToken, HttpServletResponse resp);

    void logout(HttpServletResponse resp);

    MeResponseDto me();

    void resetPassword(PasswordResetRequestDto requestDto, HttpServletResponse resp);

    void sendResetPasswordEmail(SendPasswordResetEmailRequestDto requestDto);
}
