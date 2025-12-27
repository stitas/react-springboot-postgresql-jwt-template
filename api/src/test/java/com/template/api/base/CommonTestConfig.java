package com.template.api.base;

import com.template.api.properties.JwtProperties;
import com.template.api.properties.TokenProperties;
import com.template.api.properties.UrlProperties;
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
