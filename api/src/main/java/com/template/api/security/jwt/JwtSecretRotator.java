package com.template.api.security.jwt;

import com.template.api.properties.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Service
public class JwtSecretRotator {
    private final JwtProperties jwtProperties;

    public JwtSecretRotator(
            JwtProperties jwtProperties
    ) {
        this.jwtProperties = jwtProperties;
    }

    @Scheduled(cron = "${cron.jwt-secret-refresh}")
    public void rotateSecret() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);

        String newSecret = Base64.getEncoder().encodeToString(bytes);

        // Update with new secret
        jwtProperties.setSecret(newSecret);

        log.info("JwtSecretRotator. JWT Secret rotated.");
    }
}
