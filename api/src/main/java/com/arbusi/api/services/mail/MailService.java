package com.arbusi.api.services.mail;

import com.arbusi.api.models.User;

public interface MailService {
    void sendPasswordResetEmail(User user, String token);
}

