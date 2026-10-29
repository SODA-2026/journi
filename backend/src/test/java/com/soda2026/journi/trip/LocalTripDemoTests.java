package com.soda2026.journi.trip;

import com.soda2026.journi.user.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import java.security.Principal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalTripDemoTests {
    @Test
    void suppliesIdentityOnlyForAnonymousTripCreation() throws Exception {
        var filter = new LocalTripDemoConfiguration().localTripDemoIdentity();
        for (String path : new String[]{"/api/trips", "/trips", "/users"}) {
            var request = new MockHttpServletRequest("POST", path);
            request.setServletPath(path);
            var captured = new AtomicReference<Principal>();
            filter.doFilter(request, new MockHttpServletResponse(),
                    (req, res) -> captured.set(((jakarta.servlet.http.HttpServletRequest) req).getUserPrincipal()));
            if (path.equals("/users")) assertNull(captured.get());
            else assertEquals(LocalTripDemoConfiguration.EMAIL, captured.get().getName());
        }
    }

    @Test
    void preservesRealIdentity() throws Exception {
        var request = new MockHttpServletRequest("POST", "/api/trips");
        request.setServletPath("/api/trips");
        request.setUserPrincipal(() -> "real@example.com");
        new LocalTripDemoConfiguration().localTripDemoIdentity()
                .doFilter(request, new MockHttpServletResponse(), (req, res) ->
                        assertEquals("real@example.com",
                                ((jakarta.servlet.http.HttpServletRequest) req).getUserPrincipal().getName()));
    }

    @Test
    void reusesExistingDemoUser() throws Exception {
        var users = mock(UserRepository.class);
        when(users.findByEmail(LocalTripDemoConfiguration.EMAIL)).thenReturn(Optional.of(new UserModel()));
        new LocalTripDemoConfiguration().createLocalDemoUser(users).run(null);
        verify(users, never()).saveAndFlush(any());
    }
}
