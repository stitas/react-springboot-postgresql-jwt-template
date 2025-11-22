package com.arbusi.api.services.mail.impl;

import com.arbusi.api.properties.UrlProperties;
import com.arbusi.api.models.User;
import com.arbusi.api.services.mail.MailClient;
import com.arbusi.api.services.mail.MailConstants;
import com.arbusi.api.services.mail.MailService;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    private final MailClient mailClient;
    private final UrlProperties urlProperties;

    public MailServiceImpl(
            MailClient mailClient,
            UrlProperties urlProperties
    ) {
        this.mailClient = mailClient;
        this.urlProperties = urlProperties;
    }

    @Override
    public void sendPasswordResetEmail(User user, String token) {
        String passwordResetUrl = urlProperties.frontend() + MailConstants.PASSWORD_RESET_ENDPOINT + token;
        mailClient.sendSimpleMail(
                user.getEmail(),
                MailConstants.PASSWORD_RESET_SUBJECT,
                MailConstants.PASSWORD_RESET_TEXT
                        .formatted(
                                passwordResetUrl
                        )
        );
    }
}
