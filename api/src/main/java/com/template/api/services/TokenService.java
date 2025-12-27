package com.template.api.services;

import com.template.api.enums.TokenType;
import com.template.api.models.Token;
import com.template.api.models.User;

import java.util.Optional;

public interface TokenService {
    Token createToken(User user, TokenType type);

    Optional<Token> findByToken(String token);

    Token save(Token token);

    Integer deleteExpiredAndUsedTokens();

    Token rotateRefreshToken(String oldToken);

    Integer deleteByUserAndType(User user, TokenType type);
}
