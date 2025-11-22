package com.arbusi.api.services.impl;

import com.arbusi.api.exceptions.NotAllowedException;
import com.arbusi.api.properties.TokenProperties;
import com.arbusi.api.enums.TokenType;
import com.arbusi.api.exceptions.NotFoundException;
import com.arbusi.api.models.Token;
import com.arbusi.api.models.User;
import com.arbusi.api.repositories.TokenRepository;
import com.arbusi.api.services.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final TokenProperties tokenProperties;

    public TokenServiceImpl(
            TokenRepository tokenRepository,
            TokenProperties tokenProperties
    ) {
        this.tokenRepository = tokenRepository;
        this.tokenProperties = tokenProperties;
    }

    @Override
    public Token createToken(User user, TokenType type) {
        // If user has existing token return it
        Optional<Token> tokenOpt = tokenRepository.findValidTokenByUserAndType(user, type);

        if (tokenOpt.isPresent()) {
            return tokenOpt.get();
        }

        String tokenStr = UUID.randomUUID().toString();

        Token token = new Token();
        token.setUser(user);
        token.setType(type);
        token.setUsed(false);
        token.setToken(tokenStr);

        switch (type) {
            case PASSWORD_RESET -> token.setExpireAt(LocalDateTime.now().plusSeconds(tokenProperties.passwordReset().validDurationSeconds()));
            case JWT_REFRESH -> token.setExpireAt(LocalDateTime.now().plusSeconds(tokenProperties.jwtRefresh().validDurationSeconds()));
        }

        return tokenRepository.save(token);
    }

    @Override
    @Transactional
    public Token rotateRefreshToken(String oldToken) {
        log.info(oldToken);

        Token existingToken = tokenRepository.findByToken(oldToken).orElseThrow(
                () -> new NotFoundException("Token not found")
        );

        if(LocalDateTime.now().isAfter(existingToken.getExpireAt())) {
            tokenRepository.delete(existingToken);
            throw new NotAllowedException("Token expired");
        }

        User user = existingToken.getUser();
        tokenRepository.delete(existingToken);

        return createToken(user, TokenType.JWT_REFRESH);
    }

    @Override
    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public Token save(Token token) {
        return tokenRepository.save(token);
    }

    @Override
    public Integer deleteExpiredAndUsedTokens() {
        return tokenRepository.deleteExpiredAndUsedTokens();
    }

    @Override
    @Transactional
    public Integer deleteByUserAndType(User user, TokenType type) {
        return tokenRepository.deleteByUserAndType(user, type);
    }
}

