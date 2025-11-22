package com.arbusi.api.repositories;

import com.arbusi.api.enums.TokenType;
import com.arbusi.api.models.Token;
import com.arbusi.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    @Modifying
    @Query("DELETE FROM Token token WHERE token.expireAt < CURRENT_TIMESTAMP OR token.used = true")
    Integer deleteExpiredAndUsedTokens();

    @Query("""
            SELECT token
            FROM Token token
            WHERE token.user = :user
            AND token.type = :type
            AND token.used = false
            AND token.expireAt > CURRENT_TIMESTAMP
            """)
    Optional<Token> findValidTokenByUserAndType(User user, TokenType type);

    Integer deleteByUserAndType(User user, TokenType type);
}
