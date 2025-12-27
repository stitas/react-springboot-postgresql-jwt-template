package com.template.api.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
public class OpenApiConfig {
    private static final String BEARER = "bearer";
    public static final String OAUTH_API = "Bearer Authentication";
    private static final String OAUTH_JWT = "JWT";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .components(components())
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_API));
    }

    private Components components() {
        return new Components()
                .addSecuritySchemes(OAUTH_API, new SecurityScheme().scheme(BEARER).type(SecurityScheme.Type.HTTP).bearerFormat(OAUTH_JWT));
    }
}