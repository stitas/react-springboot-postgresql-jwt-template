package com.template.api.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token")
public record TokenProperties(
        TokenData passwordReset,
        TokenData jwt,
        TokenData jwtRefresh
) {
    public record TokenData (
            int validDurationSeconds
    ) {}
}
