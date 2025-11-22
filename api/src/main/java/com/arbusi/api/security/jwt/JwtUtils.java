package com.arbusi.api.security.jwt;

import com.arbusi.api.properties.JwtProperties;
import com.arbusi.api.properties.TokenProperties;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    private final JwtProperties jwtProperties;
    private final TokenProperties tokenProperties;

    public JwtUtils(
            JwtProperties jwtProperties,
            TokenProperties tokenProperties
    ) {
        this.jwtProperties = jwtProperties;
        this.tokenProperties = tokenProperties;
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email) // email
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenProperties.jwt().validDurationSeconds() * 1000L))
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.info(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}