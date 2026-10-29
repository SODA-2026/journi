package com.soda2026.journi.trip;

import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.user.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

/** Local-only identity for manually testing trip creation before login is implemented. */
@Configuration
@Profile("local-demo")
public class LocalTripDemoConfiguration {
    @org.springframework.web.bind.annotation.RestController
    @Profile("local-demo")
    static class DemoStatusController {
        @org.springframework.web.bind.annotation.GetMapping("/api/local-demo")
        public java.util.Map<String, Boolean> status() {
            return java.util.Map.of("enabled", true);
        }
    }

    static final String EMAIL = "trip-demo@journi.invalid";

    @Bean
    ApplicationRunner createLocalDemoUser(UserRepository users) {
        return args -> {
            if (users.findByEmail(EMAIL).isPresent()) return;
            var user = new UserModel();
            user.setName("Local trip demo");
            user.setEmail(EMAIL);
            // No usable login password is assigned to this demo identity.
            user.setPasswordHash("disabled-local-demo-" + UUID.randomUUID());
            var now = Instant.now();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            users.saveAndFlush(user);
        };
    }

    @Bean
    OncePerRequestFilter localTripDemoIdentity() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain) throws ServletException, IOException {
                boolean createTrip = "POST".equals(request.getMethod())
                        && ("/api/trips".equals(request.getServletPath())
                            || "/trips".equals(request.getServletPath()));
                if (createTrip && request.getUserPrincipal() == null) {
                    chain.doFilter(new HttpServletRequestWrapper(request) {
                        @Override
                        public Principal getUserPrincipal() { return () -> EMAIL; }

                        @Override
                        public String getRemoteUser() { return EMAIL; }
                    }, response);
                } else {
                    chain.doFilter(request, response);
                }
            }
        };
    }
}
