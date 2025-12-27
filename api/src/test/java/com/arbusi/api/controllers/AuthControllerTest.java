package com.arbusi.api.controllers;

import com.arbusi.api.base.BaseTestController;
import com.arbusi.api.controllers.auth.dto.AuthRequestDto;
import com.arbusi.api.controllers.auth.dto.AuthResponseDto;
import com.arbusi.api.controllers.auth.dto.MeResponseDto;
import com.arbusi.api.controllers.auth.dto.PasswordResetRequestDto;
import com.arbusi.api.controllers.auth.dto.SendPasswordResetEmailRequestDto;
import com.arbusi.api.controllers.auth.services.AuthControllerService;
import com.arbusi.api.enums.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthControllerTest extends BaseTestController {
    private static final String BASE = "/api/v1/auth";
    private static final String REGISTER = BASE + "/register";
    private static final String LOGIN = BASE + "/login";
    private static final String RESET_PASSWORD = BASE + "/password-reset";
    private static final String SEND_RESET_PASSWORD_EMAIL = BASE + "/password-reset-request";
    private static final String ME = BASE + "/me";
    private static final String CSRF = BASE + "/csrf";
    private static final String EMAIL = "email@example.com";
    private static final String PASSWORD = "password123";
    private static final String NEW_PASSWORD = "newPass!234";
    private static final String TOKEN = "token-xyz";
    private static final long USER_ID = 101L;

    private Principal principal;

    @MockitoBean
    private AuthControllerService authControllerService;

    @BeforeEach
    void setup() {
        objectMapper.findAndRegisterModules();
        principal = () -> EMAIL;
    }

    @Test
    void whenRegister_withCsrf_then201() throws Exception {
        AuthRequestDto requestDto = createAuthRequest();
        AuthResponseDto loginResponse = createAuthResponse();

        when(authControllerService.register(any(AuthRequestDto.class), any(HttpServletResponse.class))).thenReturn(loginResponse);
        when(authControllerService.login(any(AuthRequestDto.class), any(HttpServletResponse.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post(REGISTER)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());

        verify(authControllerService).register(any(AuthRequestDto.class), any(HttpServletResponse.class));
    }

    @Test
    void whenLogin_andWithCsrf_then200AndBody() throws Exception {
        AuthRequestDto requestDto = createAuthRequest();
        AuthResponseDto responseDto = createAuthResponse();

        when(authControllerService.login(any(AuthRequestDto.class), any(HttpServletResponse.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post(LOGIN)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void whenMe_andWithAuthenticatedUser_then200AndBody() throws Exception {
        MeResponseDto meResponseDto = new MeResponseDto(USER_ID, EMAIL, UserRole.FREE);
        when(authControllerService.me(any())).thenReturn(meResponseDto);

        mockMvc.perform(get(ME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) USER_ID))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.role").value(UserRole.FREE.name()));
    }

    @Test
    void whenMe_andWithoutAuth_then401() throws Exception {
        mockMvc.perform(get(ME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenCsrf_then204() throws Exception {
        mockMvc.perform(get(CSRF))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void whenResetPassword_withCsrf_then200AndDelegatesToService() throws Exception {
        PasswordResetRequestDto dto = new PasswordResetRequestDto(TOKEN, NEW_PASSWORD);
        doNothing().when(authControllerService).resetPassword(any(PasswordResetRequestDto.class), any(HttpServletResponse.class));

        mockMvc.perform(post(RESET_PASSWORD)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        ArgumentCaptor<PasswordResetRequestDto> captor = ArgumentCaptor.forClass(PasswordResetRequestDto.class);
        verify(authControllerService).resetPassword(captor.capture(), any(HttpServletResponse.class));
        assertEquals(NEW_PASSWORD, captor.getValue().password());
        assertEquals(TOKEN, captor.getValue().token());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void whenSendResetPasswordEmail_withCsrf_then200AndDelegatesToService() throws Exception {
        SendPasswordResetEmailRequestDto dto = new SendPasswordResetEmailRequestDto(EMAIL);
        doNothing().when(authControllerService).sendResetPasswordEmail(any(SendPasswordResetEmailRequestDto.class));

        mockMvc.perform(post(SEND_RESET_PASSWORD_EMAIL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        ArgumentCaptor<SendPasswordResetEmailRequestDto> captor = ArgumentCaptor.forClass(SendPasswordResetEmailRequestDto.class);
        verify(authControllerService).sendResetPasswordEmail(captor.capture());
        assertEquals(EMAIL, captor.getValue().email());
    }

    private AuthRequestDto createAuthRequest() {
        return new AuthRequestDto(EMAIL, PASSWORD);
    }

    private AuthResponseDto createAuthResponse() {
        return new AuthResponseDto(TOKEN);
    }
}
