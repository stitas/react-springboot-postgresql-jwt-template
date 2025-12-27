package com.template.api.security;

import com.template.api.exceptions.UnauthorizedException;
import com.template.api.models.User;
import com.template.api.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailsService(
            UserService userService
    ) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email)  {
        User user = userService.findByEmail(email).orElseThrow(
                () -> new UnauthorizedException("Bad credentials")
        );

        return new SecurityUser(user);
    }
}
