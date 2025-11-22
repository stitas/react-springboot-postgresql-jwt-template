package com.arbusi.api.services;

import com.arbusi.api.enums.TokenType;
import com.arbusi.api.models.Token;
import com.arbusi.api.models.User;

import java.util.Optional;

public interface TokenService {
    Token createToken(User user, TokenType type);

    Optional<Token> findByToken(String token);

    Token save(Token token);

    Integer deleteExpiredAndUsedTokens();

    Token rotateRefreshToken(String oldToken);

    Integer deleteByUserAndType(User user, TokenType type);
}
