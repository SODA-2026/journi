package com.soda2026.journi.tripDestination;

import com.soda2026.journi.suggestion.DestinationKind;
import com.soda2026.journi.suggestion.SuggestionModel;
import com.soda2026.journi.suggestion.SuggestionType;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.tripDestination.dto.CreateTripDestinationRequest;
import com.soda2026.journi.tripMember.TripMemberId;
import com.soda2026.journi.tripMember.TripMemberModel;
import com.soda2026.journi.tripMember.TripMemberRole;
import com.soda2026.journi.user.UserModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripDestinationAccessTest {
    private final TripDestinationRepository repository = mock(TripDestinationRepository.class);
    private final EntityManager entityManager = mock(EntityManager.class);
    private final TripDestinationService service = new TripDestinationService(
            repository, entityManager, new TripDestinationMapper()
    );
    private final CreateTripDestinationRequest request = new CreateTripDestinationRequest(4L, 0);
    private final Principal principal = () -> "editor@example.com";

    @Test
    void rejectsAnonymousUser() {
        assertStatus(HttpStatus.UNAUTHORIZED, null);
        verifyNoInteractions(entityManager, repository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void rejectsMissingPrincipalName(String name) {
        assertStatus(HttpStatus.UNAUTHORIZED, () -> name);
        verifyNoInteractions(entityManager, repository);
    }

    @Test
    void rejectsUnknownUser() {
        stubCurrentUser(null);
        assertStatus(HttpStatus.UNAUTHORIZED, principal);
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsMissingTrip() {
        stubCurrentUser(user());
        assertStatus(HttpStatus.NOT_FOUND, principal);
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsNonMember() {
        stubTripAndUser();
        assertStatus(HttpStatus.FORBIDDEN, principal);
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsViewer() {
        TripModel trip = stubTripAndUser();
        when(entityManager.find(TripMemberModel.class, new TripMemberId(10L, 7L)))
                .thenReturn(new TripMemberModel(trip, user(), TripMemberRole.VIEWER));
        assertStatus(HttpStatus.FORBIDDEN, principal);
        verifyNoInteractions(repository);
    }

    @Test
    void allowsOwner() {
        TripModel trip = stubTripAndUser();
        trip.setOwnerId(7L);
        stubDestination();

        var response = service.createDestination(10L, request, principal);

        assertEquals(Long.valueOf(10L), response.tripId());
        assertEquals(Long.valueOf(4L), response.sourceSuggestionId());
        verify(repository).save(any(TripDestinationModel.class));
        verify(entityManager, never()).find(eq(TripMemberModel.class), any());
    }

    @Test
    void allowsEditor() {
        TripModel trip = stubTripAndUser();
        when(entityManager.find(TripMemberModel.class, new TripMemberId(10L, 7L)))
                .thenReturn(new TripMemberModel(trip, user(), TripMemberRole.EDITOR));
        stubDestination();

        var response = service.createDestination(10L, request, principal);

        assertEquals(Long.valueOf(10L), response.tripId());
        assertEquals(Long.valueOf(4L), response.sourceSuggestionId());
        verify(repository).save(any(TripDestinationModel.class));
    }

    private void assertStatus(HttpStatus expected, Principal requester) {
        var exception = assertThrows(ResponseStatusException.class,
                () -> service.createDestination(10L, request, requester));
        assertEquals(expected, exception.getStatusCode());
    }

    private UserModel user() {
        UserModel user = new UserModel();
        user.setId(7L);
        return user;
    }

    @SuppressWarnings("unchecked")
    private void stubCurrentUser(UserModel user) {
        TypedQuery<UserModel> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "select u from UserModel u where u.email = :email", UserModel.class
        )).thenReturn(query);
        when(query.setParameter("email", principal.getName())).thenReturn(query);
        when(query.getResultStream()).thenAnswer(invocation -> Stream.ofNullable(user));
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
        SuggestionModel city = new SuggestionModel();
        city.setId(4L);
        city.setType(SuggestionType.DESTINATION);
        city.setDestinationKind(DestinationKind.CITY);
        city.setCountry("Sverige");
        city.setName("Malmö");
        when(entityManager.find(SuggestionModel.class, 4L)).thenReturn(city);
        when(repository.save(any(TripDestinationModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }
}
