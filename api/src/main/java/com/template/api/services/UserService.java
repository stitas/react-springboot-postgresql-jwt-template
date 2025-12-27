package com.template.api.services;

import com.template.api.models.User;
import java.util.Optional;

public interface UserService {
    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByAuthSourceUserId(String authSourceUserId);

    boolean existsByEmail(String email);

    User getCurrentUser();
}
