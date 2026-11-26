package com.soda2026.journi.tripDestination;

import com.soda2026.journi.city.CityModel;
import com.soda2026.journi.country.CountryModel;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.tripDestination.dto.CreateTripDestinationRequest;
import com.soda2026.journi.tripMember.TripMemberId;
import com.soda2026.journi.trip.TripAccessService;
import com.soda2026.journi.trip.TripRepository;
import com.soda2026.journi.tripMember.TripMemberRepository;
import com.soda2026.journi.tripActivity.TripActivityRepository;
import java.util.Optional;
import java.util.List;

import com.soda2026.journi.user.UserModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripDestinationAccessTest {
    private final TripDestinationRepository repository = mock(TripDestinationRepository.class);
    private final EntityManager entityManager = mock(EntityManager.class);
    private final TripRepository trips = mock(TripRepository.class);
    private final TripMemberRepository members = mock(TripMemberRepository.class);
    private final TripActivityRepository tripActivities = mock(TripActivityRepository.class);
    private final TripDestinationService service = new TripDestinationService(
            repository, entityManager, new TripDestinationMapper(), new TripAccessService(trips, members),
            tripActivities
    );
    private final CreateTripDestinationRequest request = new CreateTripDestinationRequest(4L, 0);
    private final Long userId = 7L;

    @Test
    void rejectsUnknownUser() {
        stubCurrentUser(null);
        assertStatus(HttpStatus.NOT_FOUND, userId);
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsMissingTrip() {
        stubCurrentUser(user());
        assertStatus(HttpStatus.NOT_FOUND, userId);
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsNonMember() {
        stubTripAndUser();
        assertStatus(HttpStatus.FORBIDDEN, userId);
        verifyNoInteractions(repository);
    }

    @Test
    void allowsOwner() {
        TripModel trip = stubTripAndUser();
        trip.setOwnerId(7L);
        stubDestination();

        var response = service.createDestination(10L, request, userId);

        assertEquals(Long.valueOf(10L), response.tripId());
        assertEquals(Long.valueOf(4L), response.cityId());
        verify(repository).save(any(TripDestinationModel.class));
        verifyNoInteractions(members);
    }

    @Test
    void allowsMemberToCreateDestination() {
        stubTripAndUser();
        when(members.existsById(new TripMemberId(10L, 7L))).thenReturn(true);
        stubDestination();
        assertEquals(10L, service.createDestination(10L, request, userId).tripId());
        verify(repository).save(any());
    }

    @Test
    void rejectsOutsiderReadingDestinations() {
        stubTripAndUser();
        var error = assertThrows(ResponseStatusException.class,
                () -> service.getDestinations(10L, userId));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsOutsiderDeletingDestination() {
        stubTripAndUser();
        var error = assertThrows(ResponseStatusException.class,
                () -> service.deleteDestination(10L, 20L, userId));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verifyNoInteractions(repository, tripActivities);
    }

    @Test
    void allowsMemberReadingDestinations() {
        stubTripAndUser();
        when(members.existsById(new TripMemberId(10L, 7L))).thenReturn(true);
        when(repository.findAllByTrip_IdOrderByOrderIndexAsc(10L)).thenReturn(List.of());
        assertTrue(service.getDestinations(10L, userId).isEmpty());
    }

    private void assertStatus(HttpStatus expected, Long requester) {
        var exception = assertThrows(ResponseStatusException.class,
                () -> service.createDestination(10L, request, requester));
        assertEquals(expected, exception.getStatusCode());
    }

    private UserModel user() {
        UserModel user = new UserModel();
        user.setId(7L);
        return user;
    }

    private void stubCurrentUser(UserModel user) {
        when(entityManager.find(UserModel.class, userId)).thenReturn(user);
    }

    private TripModel stubTripAndUser() {
        stubCurrentUser(user());
        TripModel trip = new TripModel();
        trip.setId(10L);
        trip.setOwnerId(99L);
        when(entityManager.find(TripModel.class, 10L)).thenReturn(trip);
        when(trips.findById(10L)).thenReturn(Optional.of(trip));
        return trip;
    }

    private void stubDestination() {
        CountryModel country = new CountryModel();
        country.setId(1L);
        country.setName("Sverige");
        CityModel city = new CityModel();
        city.setId(4L);
        city.setCountry(country);
        city.setName("Malmö");
        when(entityManager.find(CityModel.class, 4L)).thenReturn(city);
        when(repository.save(any(TripDestinationModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }
}
