package com.arbusi.api.services.impl;

import com.arbusi.api.exceptions.UnauthorizedException;
import com.arbusi.api.models.User;
import com.arbusi.api.repositories.UserRepository;
import com.arbusi.api.security.SecurityUser;
import com.arbusi.api.services.UserService;
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
