package com.template.api.services.mail;

import com.template.api.services.mail.impl.MailClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailClientImplTest {
    private static final String TO = "test@test.com";
    private static final String SUBJECT = "SUBJECT";
    private static final String TEXT = "TEXT";

    private final ArgumentCaptor<SimpleMailMessage> messageArgumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

    private MailClientImpl emailClient;

    @Mock
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        emailClient = new MailClientImpl(javaMailSender);
    }

    @Test
    void whenSendSimpleMail_thenArgsCorrect_andMailSenderCalled() {
        emailClient.sendSimpleMail(TO, SUBJECT, TEXT);

        verify(javaMailSender).send(messageArgumentCaptor.capture());

        SimpleMailMessage capturedMessage = messageArgumentCaptor.getValue();
        assertEquals(TO, capturedMessage.getTo()[0]);
        assertEquals(SUBJECT, capturedMessage.getSubject());
        assertEquals(TEXT, capturedMessage.getText());
    }
}

