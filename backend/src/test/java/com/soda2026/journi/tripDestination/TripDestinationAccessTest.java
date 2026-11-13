package com.soda2026.journi.tripDestination;

import com.soda2026.journi.city.CityModel;
import com.soda2026.journi.country.CountryModel;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.tripDestination.dto.CreateTripDestinationRequest;
import com.soda2026.journi.tripMember.TripMemberId;
import com.soda2026.journi.tripMember.TripMemberModel;
import com.soda2026.journi.tripMember.TripMemberRole;
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
    private final TripDestinationService service = new TripDestinationService(
            repository, entityManager, new TripDestinationMapper()
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
    void rejectsViewer() {
        TripModel trip = stubTripAndUser();
        when(entityManager.find(TripMemberModel.class, new TripMemberId(10L, 7L)))
                .thenReturn(new TripMemberModel(trip, user(), TripMemberRole.MEMBER));
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
        verify(entityManager, never()).find(eq(TripMemberModel.class), any());
    }

    @Test
    void rejectsEditorUnderCurrentOwnerOnlyPolicy() {
        TripModel trip = stubTripAndUser();
        when(entityManager.find(TripMemberModel.class, new TripMemberId(10L, 7L)))
                .thenReturn(new TripMemberModel(trip, user(), TripMemberRole.OWNER));
        assertStatus(HttpStatus.FORBIDDEN, userId);
        verifyNoInteractions(repository);
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
