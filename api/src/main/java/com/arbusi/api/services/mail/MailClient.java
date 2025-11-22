package com.arbusi.api.services.mail;

public interface MailClient {
    void sendSimpleMail(String to, String subject, String text);
}