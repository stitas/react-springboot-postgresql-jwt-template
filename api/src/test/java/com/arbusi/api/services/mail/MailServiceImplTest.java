package com.arbusi.api.services.mail;

import com.arbusi.api.properties.UrlProperties;
import com.arbusi.api.models.User;
import com.arbusi.api.services.mail.impl.MailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {
    private static final String USER_EMAIL = "test@test.com";
    private static final String TOKEN = "TOKEN";
    private static final String URL = "URL";

    private final ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

    private MailServiceImpl emailService;
    private User user;

    @Mock
    private MailClient emailClient;

    @Mock
    private UrlProperties urlProperties;

    @BeforeEach
    void setUp() {
        emailService = new MailServiceImpl(emailClient, urlProperties);

        user = new User();
        user.setEmail(USER_EMAIL);
    }

    @Test
    void whenSendPasswordResetMail_thenArgsCorrect_andMailSenderCalled() {
        when(urlProperties.frontend()).thenReturn(URL);

        emailService.sendPasswordResetEmail(user, TOKEN);

        verify(emailClient).sendSimpleMail(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        List<String> args = stringArgumentCaptor.getAllValues();

        assertEquals(USER_EMAIL, args.getFirst());
        assertEquals(MailConstants.PASSWORD_RESET_SUBJECT, args.get(1));

        String emailText = MailConstants.PASSWORD_RESET_TEXT
                .formatted(
                        URL + MailConstants.PASSWORD_RESET_ENDPOINT + TOKEN
                );

        assertEquals(emailText, args.get(2));
    }
}

