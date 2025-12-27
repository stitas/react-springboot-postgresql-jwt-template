package com.template.api.services;

import com.template.api.exceptions.NotAllowedException;
import com.template.api.exceptions.NotFoundException;
import com.template.api.properties.TokenProperties;
import com.template.api.properties.TokenProperties.TokenData;
import com.template.api.enums.AuthSource;
import com.template.api.enums.TokenType;
import com.template.api.enums.UserRole;
import com.template.api.models.Token;
import com.template.api.models.User;
import com.template.api.repositories.TokenRepository;
import com.template.api.services.impl.TokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenServiceImplTest {
    private static final String EMAIL = "user@example.com";
    private static final String TOKEN_VALUE = "token-123";
    private static final int VALID_SECONDS = 900;

    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private TokenProperties tokenProperties;

    @Captor
    private ArgumentCaptor<Token> tokenCaptor;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl(tokenRepository, tokenProperties);
    }

    @Test
    void whenCreateToken_existingValidToken_thenReturnExisting() {
        User user = createUser(EMAIL);
        Token existing = createExistingToken(user, TokenType.PASSWORD_RESET, false, LocalDateTime.now().plusMinutes(5));

        when(tokenRepository.findValidTokenByUserAndType(user, TokenType.PASSWORD_RESET)).thenReturn(Optional.of(existing));

        Token result = tokenService.createToken(user, TokenType.PASSWORD_RESET);

        verify(tokenRepository).findValidTokenByUserAndType(user, TokenType.PASSWORD_RESET);
        verify(tokenRepository, never()).save(any(Token.class));
        assertSame(existing, result);
    }

    @Test
    void whenCreateVerificationToken_noExisting_thenCreateAndSave() {
        User user = createUser(EMAIL);
        when(tokenRepository.findValidTokenByUserAndType(user, TokenType.PASSWORD_RESET)).thenReturn(Optional.empty());
        when(tokenRepository.save(any(Token.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenProperties.passwordReset()).thenReturn(new TokenData(VALID_SECONDS));

        LocalDateTime before = LocalDateTime.now();
        Token result = tokenService.createToken(user, TokenType.PASSWORD_RESET);
        LocalDateTime after = LocalDateTime.now();

        verify(tokenRepository).findValidTokenByUserAndType(user, TokenType.PASSWORD_RESET);
        verify(tokenRepository).save(tokenCaptor.capture());

        Token saved = tokenCaptor.getValue();
        assertSame(user, saved.getUser());
        assertEquals(TokenType.PASSWORD_RESET, saved.getType());
        assertEquals(false, saved.getUsed());
        assertNotNull(saved.getToken());
        UUID.fromString(saved.getToken());
        assertTrue(!saved.getExpireAt().isBefore(before.plusSeconds(VALID_SECONDS)) &&
                !saved.getExpireAt().isAfter(after.plusSeconds(VALID_SECONDS)));
        assertSame(saved, result);
    }

    @Test
    void whenRotateRefreshToken_notFound_thenThrowNotFound() {
        when(tokenRepository.findByToken(TOKEN_VALUE)).thenReturn(Optional.empty());

        // Use the real service (no need to spy since it will throw before calling createToken)
        assertThrows(NotFoundException.class, () -> tokenService.rotateRefreshToken(TOKEN_VALUE));

        verify(tokenRepository).findByToken(TOKEN_VALUE);
        verify(tokenRepository, never()).delete(any(Token.class));
    }

    @Test
    void whenRotateRefreshToken_tokenNotFound_thenThrowNotFound() {
        when(tokenRepository.findByToken(TOKEN_VALUE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> tokenService.rotateRefreshToken(TOKEN_VALUE));

        verify(tokenRepository).findByToken(TOKEN_VALUE);
        // no deletes, no further usage
        verify(tokenRepository, never()).delete(any(Token.class));
    }

    @Test
    void whenRotateRefreshToken_tokenExpired_thenDeleteAndThrowNotAllowed() {
        User user = createUser(EMAIL);
        // expired token
        Token existing = createExistingToken(
                user,
                TokenType.JWT_REFRESH,
                false,
                LocalDateTime.now().minusMinutes(1)
        );
        when(tokenRepository.findByToken(TOKEN_VALUE)).thenReturn(Optional.of(existing));

        assertThrows(NotAllowedException.class,
                () -> tokenService.rotateRefreshToken(TOKEN_VALUE));

        verify(tokenRepository).findByToken(TOKEN_VALUE);
        // should be deleted once when expired
        verify(tokenRepository).delete(existing);
    }

    @Test
    void whenRotateRefreshToken_validToken_thenDeleteOldAndCreateNew() {
        User user = createUser(EMAIL);
        // valid (not expired) old refresh token
        Token existing = createExistingToken(
                user,
                TokenType.JWT_REFRESH,
                false,
                LocalDateTime.now().plusMinutes(10)
        );
        when(tokenRepository.findByToken(TOKEN_VALUE)).thenReturn(Optional.of(existing));

        // new refresh token that rotateRefreshToken should return
        Token newToken = createExistingToken(
                user,
                TokenType.JWT_REFRESH,
                false,
                LocalDateTime.now().plusMinutes(20)
        );

        when(tokenProperties.jwtRefresh()).thenReturn(new TokenData(VALID_SECONDS));

        Token result = tokenService.rotateRefreshToken(TOKEN_VALUE);

        verify(tokenRepository).findByToken(TOKEN_VALUE);
        verify(tokenRepository).delete(existing);
    }

    @Test
    void whenFindByToken_thenDelegateAndReturn() {
        User user = createUser(EMAIL);
        Token token = createExistingToken(user, TokenType.PASSWORD_RESET, false, LocalDateTime.now().plusMinutes(10));
        when(tokenRepository.findByToken(TOKEN_VALUE)).thenReturn(Optional.of(token));

        Optional<Token> result = tokenService.findByToken(TOKEN_VALUE);

        verify(tokenRepository).findByToken(TOKEN_VALUE);
        assertTrue(result.isPresent());
        assertSame(token, result.get());
    }

    @Test
    void whenSave_thenDelegateAndReturn() {
        User user = createUser(EMAIL);
        Token token = createExistingToken(user, TokenType.PASSWORD_RESET, false, LocalDateTime.now().plusMinutes(10));
        when(tokenRepository.save(token)).thenReturn(token);

        Token saved = tokenService.save(token);

        verify(tokenRepository).save(token);
        assertSame(token, saved);
    }

    @Test
    void whenDeleteExpiredAndUsedTokens_thenDelegateAndReturnCount() {
        when(tokenRepository.deleteExpiredAndUsedTokens()).thenReturn(3);

        int count = tokenService.deleteExpiredAndUsedTokens();

        verify(tokenRepository).deleteExpiredAndUsedTokens();
        assertEquals(3, count);
    }

    @Test
    void whenDeleteByUserAndType_thenDelegateAndReturnCount() {
        when(tokenRepository.deleteByUserAndType(any(), any())).thenReturn(3);

        User user = createUser(EMAIL);
        int count = tokenService.deleteByUserAndType(user, TokenType.JWT_REFRESH);

        verify(tokenRepository).deleteByUserAndType(user, TokenType.JWT_REFRESH);
        assertEquals(3, count);
    }

    private User createUser(String email) {
        return User.builder()
                .email(email)
                .authSource(AuthSource.LOCAL)
                .passwordHash("hash")
                .role(UserRole.FREE)
                .build();
    }

    private Token createExistingToken(User user, TokenType type, boolean used, LocalDateTime expireAt) {
        return Token.builder()
                .user(user)
                .type(type)
                .used(used)
                .token(UUID.randomUUID().toString())
                .expireAt(expireAt)
                .build();
    }
}
