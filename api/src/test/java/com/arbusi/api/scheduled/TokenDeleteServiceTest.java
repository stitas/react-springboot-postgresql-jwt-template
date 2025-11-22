package com.arbusi.api.scheduled;

import com.arbusi.api.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenDeleteServiceTest {
    private TokenDeleteService tokenDeleteService;

    @Mock
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenDeleteService = new TokenDeleteService(tokenService);
    }

    @Test
    void whenDeleteToken_thenUserTokenServiceDeleteAllCalled() {
        tokenDeleteService.deleteExpiredAndVerifiedUserTokens();
        verify(tokenService).deleteExpiredAndUsedTokens();
    }
}
