package com.template.api.security;

import com.template.api.enums.AuthSource;
import com.template.api.enums.UserRole;
import com.template.api.models.User;
import com.template.api.services.UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    private final OidcUserService delegate = new OidcUserService();
    private final UserService userService;

    public CustomOidcUserService(
            UserService userService
    ) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
        OidcUser user = delegate.loadUser(request); // fetch from Google
        String sub   = user.getSubject();           // stable id
        String email = user.getEmail();             // may be null if not granted

        userService.findByAuthSourceUserId(sub).orElseGet(() -> {
            var u = new User();
            u.setAuthSource(AuthSource.GOOGLE);
            u.setAuthSourceUserId(sub);
            u.setEmail(email);
            u.setRole(UserRole.FREE);
            return userService.save(u);
        });

        return user; // hand back to Spring Security
    }
}
