package com.soda2026.journi.trip;

import com.soda2026.journi.tripDestination.*;
import com.soda2026.journi.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CreateTripTests {
    private TripRepository trips;
    private TripDestinationRepository destinations;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        trips = mock(TripRepository.class);
        destinations = mock(TripDestinationRepository.class);
        var users = mock(UserRepository.class);
        var owner = new UserModel();
        owner.setId(7L);
        when(users.findByEmail("test@example.com")).thenReturn(Optional.of(owner));
        when(trips.saveAndFlush(any())).thenAnswer(invocation -> {
            TripModel trip = invocation.getArgument(0);
            trip.setId(42L);
            return trip;
        });
        var service = new TripService(trips, new TripMapper(), users, destinations);
        mvc = MockMvcBuilders.standaloneSetup(new TripController(service))
                .setControllerAdvice(new TripExceptionHandler()).build();
    }

    @Test
    void createsTripAndFirstDestinationForAuthenticatedOwner() throws Exception {
        mvc.perform(post("/api/trips").principal(() -> "test@example.com")
                .contentType("application/json").content(payload(" Italien ", " Rom ", "2027-06-10")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.country").value("Italien"))
                .andExpect(jsonPath("$.city").value("Rom"));
        verify(trips).saveAndFlush(argThat(trip -> trip.getOwnerId().equals(7L)
                && trip.getCreatedAt() != null && trip.getUpdatedAt() != null));
        verify(destinations).saveAndFlush(argThat(destination ->
                destination.getTrip().getId().equals(42L)
                && destination.getCountry().equals("Italien")
                && destination.getPlaceName().equals("Rom")
                && destination.getOrderIndex() == 0));
    }

    @Test
    void rejectsMissingDestinationWithoutSaving() throws Exception {
        for (String[] destination : new String[][]{{" ", "Rom"}, {"Italien", " "}}) {
            mvc.perform(post("/api/trips").principal(() -> "test@example.com")
                    .contentType("application/json")
                    .content(payload(destination[0], destination[1], "2027-06-10")))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(trips, destinations);
    }

    @Test
    void rejectsReversedDatesWithoutSaving() throws Exception {
        mvc.perform(post("/api/trips").principal(() -> "test@example.com")
                .contentType("application/json").content(payload("Italien", "Rom", "2027-06-01")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(trips, destinations);
    }

    @Test
    void requiresAuthentication() throws Exception {
        mvc.perform(post("/api/trips").contentType("application/json")
                .content(payload("Italien", "Rom", "2027-06-10")))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(trips, destinations);
    }

    private String payload(String country, String city, String end) {
        return """
                {"country":"%s","city":"%s","startDate":"2027-06-05","endDate":"%s"}
                """.formatted(country, city, end);
    }
}
