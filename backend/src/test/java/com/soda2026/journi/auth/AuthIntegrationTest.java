package com.soda2026.journi.auth;

import com.soda2026.journi.security.SecurityConfig;
import com.soda2026.journi.user.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.mock.web.MockHttpSession;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(AuthIntegrationTest.Config.class)
@WebAppConfiguration
class AuthIntegrationTest {
    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    @Import({SecurityConfig.class, AuthController.class, AuthService.class, UserService.class, UserMapper.class,
            com.soda2026.journi.trip.TripController.class,
            com.soda2026.journi.tripDestination.TripDestinationController.class})
    static class Config {
        @Bean com.soda2026.journi.trip.TripService trips() { return mock(com.soda2026.journi.trip.TripService.class); }
        @Bean com.soda2026.journi.tripDestination.TripDestinationService destinations() {
            return mock(com.soda2026.journi.tripDestination.TripDestinationService.class);
        }
        @Bean UserRepository users() { return mock(UserRepository.class); }
    }
    @Autowired WebApplicationContext context;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;
    @Autowired com.soda2026.journi.trip.TripService trips;
    @Autowired com.soda2026.journi.tripDestination.TripDestinationService destinations;
    MockMvc mvc;
    @BeforeEach void setup() {
        reset(users, trips, destinations);
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        UserModel user = new UserModel("olena@example.com", "Olena", encoder.encode("password123"), null);
        user.setId(42L);
        when(users.findByEmail("olena@example.com")).thenReturn(Optional.of(user));
    }
    @Test void loginPersistsSessionRotatesIdAndLogoutEndsIt() throws Exception {
        var session = new MockHttpSession();
        String oldId = session.getId();
        mvc.perform(post("/api/auth/login").session(session).with(csrf())
                .contentType("application/json")
                .content("{\"email\":\"olena@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.password").doesNotExist());
        assertNotEquals(oldId, session.getId());
        mvc.perform(get("/api/auth/me").session(session)).andExpect(status().isOk());
        mvc.perform(post("/api/auth/logout").session(session).with(csrf())).andExpect(status().isNoContent());
        assertTrue(session.isInvalid());
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }
    @Test void rejectsBadPasswordAndMissingCsrf() throws Exception {
        mvc.perform(post("/api/auth/login").with(csrf()).contentType("application/json")
                .content("{\"email\":\"olena@example.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.message").value("Invalid credentials"));
        mvc.perform(post("/api/auth/login").contentType("application/json")
                .content("{\"email\":\"olena@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isForbidden());
    }
    @Test void registrationHashesPasswordAndSignsIn() throws Exception {
        when(users.saveAndFlush(any())).thenAnswer(call -> {
            UserModel user = call.getArgument(0);
            assertTrue(encoder.matches("password123", user.getPasswordHash()));
            assertNull(user.getLastLogin());
            user.setId(99L);
            when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            return user;
        });
        var result = mvc.perform(post("/api/auth/register").with(csrf()).contentType("application/json")
                .content("{\"name\":\"New user\",\"email\":\"new@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.passwordHash").doesNotExist()).andReturn();
        mvc.perform(get("/api/auth/me").session((MockHttpSession) result.getRequest().getSession(false)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("new@example.com"));
    }
    @Test void validatesRegistrationAndRejectsDuplicateEmail() throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType("application/json")
                .content("{\"name\":\"\",\"email\":\"invalid\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest());
        when(users.existsByEmail("olena@example.com")).thenReturn(true);
        mvc.perform(post("/api/auth/register").with(csrf()).contentType("application/json")
                .content("{\"name\":\"Olena\",\"email\":\"olena@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email is already registered"));
    }

    @Test void missingNameReturnsValidationMessageWithoutCreatingUser() throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType("application/json")
                .content("{\"name\":\"\",\"email\":\"new@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name is required"))
                .andExpect(jsonPath("$.password").doesNotExist());
        verify(users, never()).saveAndFlush(any());
    }

    @Test void shortPasswordReturnsRequirementWithoutCreatingUser() throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType("application/json")
                .content("{\"name\":\"New user\",\"email\":\"new@example.com\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password must contain at least 8 characters"))
                .andExpect(jsonPath("$.password").doesNotExist());
        verify(users, never()).saveAndFlush(any());
    }

    @Test void expiredSessionWithOldCsrfReturnsUnauthorizedWithoutCreatingTrip() throws Exception {
        mvc.perform(post("/trips/create")
                .with(request -> {
                    request.setRequestedSessionId("expired-session");
                    request.setRequestedSessionIdValid(false);
                    return request;
                })
                .header("X-CSRF-TOKEN", "old-token")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication required"));
        verifyNoInteractions(trips);
    }

    @Test void browserCsrfTokenMustBeRefreshedAfterLogin() throws Exception {
        var tokenResult = mvc.perform(get("/api/auth/csrf")).andExpect(status().isOk()).andReturn();
        var session = (MockHttpSession) tokenResult.getRequest().getSession(false);
        var token = (org.springframework.security.web.csrf.CsrfToken)
                tokenResult.getRequest().getAttribute(org.springframework.security.web.csrf.CsrfToken.class.getName());
        mvc.perform(post("/api/auth/login").session(session).header(token.getHeaderName(), token.getToken())
                .contentType("application/json")
                .content("{\"email\":\"olena@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/auth/logout").session(session).header(token.getHeaderName(), token.getToken()))
                .andExpect(status().isForbidden());
        var freshResult = mvc.perform(get("/api/auth/csrf").session(session)).andReturn();
        var fresh = (org.springframework.security.web.csrf.CsrfToken)
                freshResult.getRequest().getAttribute(org.springframework.security.web.csrf.CsrfToken.class.getName());
        mvc.perform(post("/api/auth/logout").session(session).header(fresh.getHeaderName(), fresh.getToken()))
                .andExpect(status().isNoContent());
    }
    @Test void tripsUseSessionIdentityAndRejectAnotherOwner() throws Exception {
        var model = new UserModel("olena@example.com", "Olena", "hash", null);
        model.setId(42L);
        var user = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                .user(new com.soda2026.journi.security.UserPrincipal(model));
        mvc.perform(get("/trips/mine").with(user)).andExpect(status().isOk());
        verify(trips).findTripsByOwnerId(42L);
        mvc.perform(get("/trips/7").with(user)).andExpect(status().isForbidden());
        verify(trips, never()).findTripsByOwnerId(7L);
        mvc.perform(post("/api/trips/10/destinations").with(user).with(csrf())
                .contentType("application/json").content("{\"cityId\":4,\"orderIndex\":0}"))
                .andExpect(status().isCreated());
        verify(destinations).createDestination(eq(10L), any(), eq(42L));
    }
    @Test void issuesCsrfTokenAndRejectsAnonymousRequests() throws Exception {
        mvc.perform(get("/api/auth/csrf")).andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty());
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }
}
