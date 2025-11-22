package com.arbusi.api.services.impl;

import com.arbusi.api.enums.AuthSource;
import com.arbusi.api.enums.UserRole;
import com.arbusi.api.models.User;
import com.arbusi.api.repositories.UserRepository;
import com.arbusi.api.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static final String EMAIL = "user@example.com";
    private static final String OTHER_EMAIL = "other@example.com";
    private static final String AUTH_SOURCE_USER_ID = "google-123";
    private static final String PASSWORD_HASH = "hash123";

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void whenSave_thenDelegatesAndReturnsSaved() {
        User input = createUser(EMAIL, AUTH_SOURCE_USER_ID, PASSWORD_HASH);
        User saved = createUser(EMAIL, AUTH_SOURCE_USER_ID, PASSWORD_HASH);

        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.save(input);

        verify(userRepository).save(userCaptor.capture());
        assertSame(saved, result);
        assertEquals(EMAIL, userCaptor.getValue().getEmail());
        assertEquals(AuthSource.LOCAL, userCaptor.getValue().getAuthSource());
        assertEquals(UserRole.FREE, userCaptor.getValue().getRole());
    }

    @Test
    void whenFindByEmail_andFound_thenReturnUser() {
        User user = createUser(EMAIL, AUTH_SOURCE_USER_ID, PASSWORD_HASH);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(EMAIL);

        verify(userRepository).findByEmail(EMAIL);
        assertTrue(result.isPresent());
        assertSame(user, result.get());
    }

    @Test
    void whenFindByEmail_andNotFound_thenReturnEmpty() {
        when(userRepository.findByEmail(OTHER_EMAIL)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail(OTHER_EMAIL);

        verify(userRepository).findByEmail(OTHER_EMAIL);
        assertFalse(result.isPresent());
    }

    @Test
    void whenFindByAuthSourceUserId_andFound_thenReturnUser() {
        User user = createUser(EMAIL, AUTH_SOURCE_USER_ID, PASSWORD_HASH);
        when(userRepository.findByAuthSourceUserId(AUTH_SOURCE_USER_ID)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByAuthSourceUserId(AUTH_SOURCE_USER_ID);

        verify(userRepository).findByAuthSourceUserId(AUTH_SOURCE_USER_ID);
        assertTrue(result.isPresent());
        assertSame(user, result.get());
    }

    @Test
    void whenFindByAuthSourceUserId_andNotFound_thenReturnEmpty() {
        when(userRepository.findByAuthSourceUserId("missing")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByAuthSourceUserId("missing");

        verify(userRepository).findByAuthSourceUserId("missing");
        assertFalse(result.isPresent());
    }

    @Test
    void whenExistsByEmail_andTrue_thenReturnTrue() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        boolean exists = userService.existsByEmail(EMAIL);

        verify(userRepository).existsByEmail(EMAIL);
        assertTrue(exists);
    }

    @Test
    void whenExistsByEmail_andFalse_thenReturnFalse() {
        when(userRepository.existsByEmail(OTHER_EMAIL)).thenReturn(false);

        boolean exists = userService.existsByEmail(OTHER_EMAIL);

        verify(userRepository).existsByEmail(OTHER_EMAIL);
        assertFalse(exists);
    }

    private User createUser(String email, String authSourceUserId, String passwordHash) {
        return User.builder()
                .email(email)
                .authSource(AuthSource.LOCAL)
                .authSourceUserId(authSourceUserId)
                .passwordHash(passwordHash)
                .role(UserRole.FREE)
                .build();
    }
}
