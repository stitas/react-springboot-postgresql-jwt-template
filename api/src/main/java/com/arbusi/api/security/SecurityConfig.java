package com.arbusi.api.security;

import com.arbusi.api.controllers.auth.dto.AuthResponseDto;
import com.arbusi.api.properties.UrlProperties;
import com.arbusi.api.security.jwt.JwtAuthTokenFilter;
import com.arbusi.api.security.jwt.JwtUtils;
import com.arbusi.api.services.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class SecurityConfig {
    public static final String[] ENDPOINTS_WITHOUT_AUTH = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/csrf",
            "/api/v1/auth/password-reset",
            "/api/v1/auth/password-reset-request",
            "/api/v1/auth/refresh",
            "/api/v1/auth/oauth/google",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error"
    };

    private static final String OAUTH_BASE_URI = "/api/v1/auth/oauth";

    private final CustomUserDetailsService userDetailsService;
    private final CustomOidcUserService oidcUserService;
    private final UrlProperties urlProperties;
    private final JwtUtils jwtUtils;
    private final OAuth2Service oAuth2Service;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            CustomOidcUserService oidcUserService,
            UrlProperties urlProperties,
            JwtUtils jwtUtils,
            OAuth2Service oAuth2Service
    ) {
        this.userDetailsService = userDetailsService;
        this.oidcUserService = oidcUserService;
        this.urlProperties = urlProperties;
        this.jwtUtils = jwtUtils;
        this.oAuth2Service = oAuth2Service;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain cookieCsrfChain(HttpSecurity http) throws Exception {
        OrRequestMatcher cookieEndpoints = new OrRequestMatcher(
                PathPatternRequestMatcher.withDefaults().matcher("/api/v1/auth/refresh"),
                PathPatternRequestMatcher.withDefaults().matcher("/api/v1/auth/csrf")
        );

        // For SPA with CookieCsrfTokenRepository, disable XOR masking so header matches cookie
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);

        http
                .securityMatcher(cookieEndpoints)
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(requestHandler)
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/refresh", "/api/v1/auth/csrf").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiBearerChain(HttpSecurity http) throws Exception {
        JwtAuthTokenFilter jwtFilter = new JwtAuthTokenFilter(jwtUtils, userDetailsService);

        http
                .securityMatcher(new OrRequestMatcher(
                                PathPatternRequestMatcher.withDefaults().matcher("/api/**"),
                                PathPatternRequestMatcher.withDefaults().matcher("/login/oauth2/**")
                        )
                )
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ENDPOINTS_WITHOUT_AUTH).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(ui -> ui.oidcUserService(oidcUserService))
                        .authorizationEndpoint(a -> a.baseUri(OAUTH_BASE_URI))
                        .successHandler((req, res, auth) -> {
                                    AuthResponseDto jwtDto = oAuth2Service.loginOauth2(auth, res);
                                    String redirectUrl = urlProperties.frontend() + "/oauth2/success?token=" + URLEncoder.encode(jwtDto.token(), StandardCharsets.UTF_8);
                                    res.sendRedirect(redirectUrl);
                                }
                        )
                        .failureHandler((req, res, e) ->
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 authentication failed")
                        )
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                urlProperties.frontend()
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-XSRF-TOKEN",
                "X-Requested-With",
                "Accept"
        ));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
