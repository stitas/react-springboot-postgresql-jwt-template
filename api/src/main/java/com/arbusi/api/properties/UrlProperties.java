package com.arbusi.api.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "url")
public record UrlProperties(
        String frontend
) {}
