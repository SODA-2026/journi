package com.soda2026.journi.auth;

import com.soda2026.journi.user.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager manager;
    private final SessionAuthenticationStrategy sessions;
    private final SecurityContextRepository contexts;
    private final CsrfTokenRepository csrf;
    private final UserService users;

    public AuthService(AuthenticationManager manager, SessionAuthenticationStrategy sessions,
            SecurityContextRepository contexts, CsrfTokenRepository csrf, UserService users) {
        this.manager = manager;
        this.sessions = sessions;
        this.contexts = contexts;
        this.csrf = csrf;
        this.users = users;
    }

    public UserResponseDTO register(UserRequestDTO data, HttpServletRequest request, HttpServletResponse response) {
        users.createUser(data);
        return login(new LoginRequestDTO(data.getEmail(), data.getPassword()), request, response);
    }

    public UserResponseDTO login(LoginRequestDTO data, HttpServletRequest request, HttpServletResponse response) {
        var authentication = manager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(data.email().trim(), data.password()));
        sessions.onAuthentication(authentication, request, response);
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        contexts.saveContext(context, request, response);
        csrf.saveToken(null, request, response);
        return users.findByEmail(authentication.getName());
    }
}
