package com.template.api.scheduled;

import com.template.api.services.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenDeleteService {
    private final TokenService tokenService;

    public TokenDeleteService(
            TokenService tokenService
    ) {
        this.tokenService = tokenService;
    }

    @Scheduled(cron = "${cron.user-token-delete}")
    public void deleteExpiredAndVerifiedUserTokens() {
        log.info("TokenDeleteService. verified/expired user token deletion job started");

        int deleted = tokenService.deleteExpiredAndUsedTokens();

        log.info("TokenDeleteService. {} tokens deleted", deleted);

        log.info("TokenDeleteService. verified/expired user token deletion job finished");
    }
}
