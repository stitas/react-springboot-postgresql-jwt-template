package com.template.api.services.impl;

import com.template.api.exceptions.UnauthorizedException;
import com.template.api.models.User;
import com.template.api.repositories.UserRepository;
import com.template.api.security.SecurityUser;
import com.template.api.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByAuthSourceUserId(String authSourceUserId) {
        return userRepository.findByAuthSourceUserId(authSourceUserId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUser(User user))) {
            throw new UnauthorizedException("Bad credentials");
        }
        return user;
    }
}
