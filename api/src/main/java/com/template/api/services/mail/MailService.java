package com.template.api.services.mail;

import com.template.api.models.User;

public interface MailService {
    void sendPasswordResetEmail(User user, String token);
}

