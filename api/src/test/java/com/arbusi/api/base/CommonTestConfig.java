package com.arbusi.api.base;

import com.arbusi.api.properties.JwtProperties;
import com.arbusi.api.properties.TokenProperties;
import com.arbusi.api.properties.UrlProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@EnableConfigurationProperties({
        UrlProperties.class,
        TokenProperties.class,
        JwtProperties.class
})
public class CommonTestConfig {
}
