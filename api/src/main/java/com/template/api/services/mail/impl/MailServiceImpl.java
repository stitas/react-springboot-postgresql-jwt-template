package com.template.api.services.mail.impl;

import com.template.api.properties.UrlProperties;
import com.template.api.models.User;
import com.template.api.services.mail.MailClient;
import com.template.api.services.mail.MailConstants;
import com.template.api.services.mail.MailService;
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
