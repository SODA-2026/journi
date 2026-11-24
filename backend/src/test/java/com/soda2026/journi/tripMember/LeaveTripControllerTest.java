package com.soda2026.journi.tripMember;

import com.soda2026.journi.security.SecurityConfig;
import com.soda2026.journi.security.UserPrincipal;
import com.soda2026.journi.trip.TripAccessService;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.trip.TripRepository;
import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringJUnitConfig(LeaveTripControllerTest.Config.class)
@WebAppConfiguration
class LeaveTripControllerTest {
    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    @Import({SecurityConfig.class, TripMemberController.class, TripMemberService.class, TripAccessService.class})
    static class Config {
        @Bean TripRepository trips() { return mock(TripRepository.class); }
        @Bean TripMemberRepository members() { return mock(TripMemberRepository.class); }
        @Bean UserRepository users() { return mock(UserRepository.class); }
        @Bean EntityManager entityManager() { return mock(EntityManager.class); }
    }

    @Autowired WebApplicationContext context;
    @Autowired TripRepository trips;
    @Autowired TripMemberRepository members;
    @Autowired EntityManager entities;
    @Autowired TripAccessService access;
    private MockMvc mvc;
    private TripMemberModel membership;

    private UserPrincipal principal(long id) {
        var user = new UserModel("user" + id + "@example.com", "User " + id, "hash", null);
        user.setId(id);
        return new UserPrincipal(user);
    }

    @BeforeEach
    void setup() {
        reset(trips, members, entities);
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        var trip = new TripModel();
        trip.setId(10L);
        trip.setOwnerId(1L);
        var user = new UserModel("member@example.com", "Member", "hash", null);
        user.setId(2L);
        membership = new TripMemberModel(trip, user);
        when(trips.findById(10L)).thenReturn(Optional.of(trip));
        when(entities.find(TripModel.class, 10L)).thenReturn(trip);
        when(members.findById(new TripMemberId(10L, 2L))).thenReturn(Optional.of(membership));
        when(members.existsById(new TripMemberId(10L, 2L))).thenReturn(true);
        when(members.existsById(new TripMemberId(10L, 3L))).thenReturn(true);
    }

    @Test
    void leavesOnlyOwnMembershipEvenWhenAnotherUserIdIsSubmitted() throws Exception {
        doAnswer(call -> {
            when(members.existsById(new TripMemberId(10L, 2L))).thenReturn(false);
            return null;
        }).when(members).delete(membership);

        mvc.perform(delete("/api/trips/10/members/me").with(user(principal(2))).with(csrf())
                .contentType("application/json").content("{\"userId\":3}"))
                .andExpect(status().isNoContent());

        verify(members).delete(membership);
        verify(entities).persist(argThat(event -> event instanceof TripMemberEventModel memberEvent
                && memberEvent.getTripId().equals(10L) && memberEvent.getMemberName().equals("Member")));
        verify(members, never()).findById(new TripMemberId(10L, 3L));
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> access.requireTripOwnerOrMember(10L, 2L));
        assertDoesNotThrow(() -> access.requireTripOwnerOrMember(10L, 1L));
        assertDoesNotThrow(() -> access.requireTripOwnerOrMember(10L, 3L));
        verify(trips, never()).delete(any());
        verify(entities, never()).remove(any());
    }

    @Test
    void ownerCannotLeave() throws Exception {
        mvc.perform(delete("/api/trips/10/members/me").with(user(principal(1))).with(csrf()))
                .andExpect(status().isBadRequest());
        verify(members, never()).delete(any());
        verify(entities, never()).persist(any());
    }

    @Test
    void outsiderCannotLeaveOrRemoveAnotherMember() throws Exception {
        mvc.perform(delete("/api/trips/10/members/me").with(user(principal(4))).with(csrf()))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/trips/10/members/3").with(user(principal(2))).with(csrf()))
                .andExpect(status().isForbidden());
        verify(members, never()).delete(any());
    }

    @Test
    void requiresAuthenticationAndCsrf() throws Exception {
        mvc.perform(delete("/api/trips/10/members/me").with(csrf()))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/trips/10/members/me").with(user(principal(2))))
                .andExpect(status().isForbidden());
        verify(members, never()).delete(any());
    }

    @Test
    void missingTripDoesNotDeleteMembership() throws Exception {
        mvc.perform(delete("/api/trips/99/members/me").with(user(principal(2))).with(csrf()))
                .andExpect(status().isNotFound());
        verify(members, never()).delete(any());
    }

    @Test
    void onlyOwnerAndRemainingMembersCanReadDepartureHistory() throws Exception {
        @SuppressWarnings("unchecked")
        var query = (jakarta.persistence.TypedQuery<TripMemberEventModel>) mock(jakarta.persistence.TypedQuery.class);
        when(entities.createQuery(anyString(), eq(TripMemberEventModel.class))).thenReturn(query);
        when(query.setParameter("tripId", 10L)).thenReturn(query);
        when(query.setMaxResults(50)).thenReturn(query);
        when(query.getResultList()).thenReturn(java.util.List.of(new TripMemberEventModel(10L, "Member")));

        for (long id : new long[]{1, 3}) {
            mvc.perform(get("/api/trips/10/members/events").with(user(principal(id))))
                    .andExpect(status().isOk()).andExpect(jsonPath("$[0].memberName").value("Member"));
        }
        when(members.existsById(new TripMemberId(10L, 2L))).thenReturn(false);
        mvc.perform(get("/api/trips/10/members/events").with(user(principal(2))))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/trips/10/members/events").with(user(principal(4))))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/trips/10/members/events"))
                .andExpect(status().isUnauthorized());
        verify(query, times(2)).getResultList();
    }

    @Test
    void notificationFailurePreventsDeletingMembership() {
        doThrow(new IllegalStateException("Event could not be stored"))
                .when(entities).persist(any(TripMemberEventModel.class));
        var service = context.getBean(TripMemberService.class);
        assertThrows(IllegalStateException.class, () -> service.leaveTrip(10L, 2L));
        verify(members, never()).delete(any());
    }
}
