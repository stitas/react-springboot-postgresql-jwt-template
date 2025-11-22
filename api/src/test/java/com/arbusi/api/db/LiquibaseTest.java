package com.arbusi.api.db;

import com.arbusi.api.models.User;
import com.arbusi.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LiquibaseTest {

    @Autowired
    private UserRepository repository;

    @Test
    void whenAppStarts_thenUsersFromLiquibaseScriptCreated() {
        List<User> users = repository.findAll();
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }
}
