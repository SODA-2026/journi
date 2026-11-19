package com.soda2026.journi.trip;

import com.soda2026.journi.tripMember.TripMemberId;
import com.soda2026.journi.tripMember.TripMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripAccessServiceTest {
    private final TripRepository trips = mock(TripRepository.class);
    private final TripMemberRepository members = mock(TripMemberRepository.class);
    private final TripAccessService service = new TripAccessService(trips, members);

    private void existingTrip() {
        TripModel trip = new TripModel();
        trip.setId(10L);
        trip.setOwnerId(1L);
        when(trips.findById(10L)).thenReturn(Optional.of(trip));
    }

    @Test
    void ownerHasAccessWithoutMembership() {
        existingTrip();
        assertTrue(service.isOwner(10L, 1L));
        assertDoesNotThrow(() -> service.requireTripOwner(10L, 1L));
        assertDoesNotThrow(() -> service.requireTripOwnerOrMember(10L, 1L));
        verifyNoInteractions(members);
    }

    @Test
    void memberHasAccessButCannotPerformOwnerActions() {
        existingTrip();
        when(members.existsById(new TripMemberId(10L, 2L))).thenReturn(true);
        assertTrue(service.isMember(10L, 2L));
        assertFalse(service.isOwner(10L, 2L));
        assertDoesNotThrow(() -> service.requireTripOwnerOrMember(10L, 2L));
        assertStatus(HttpStatus.FORBIDDEN, () -> service.requireTripOwner(10L, 2L));
    }

    @Test
    void outsiderHasNoAccess() {
        existingTrip();
        assertFalse(service.isMember(10L, 3L));
        assertStatus(HttpStatus.FORBIDDEN, () -> service.requireTripOwnerOrMember(10L, 3L));
    }

    @Test
    void missingTripReturnsNotFound() {
        assertStatus(HttpStatus.NOT_FOUND, () -> service.requireTripOwner(10L, 1L));
        assertStatus(HttpStatus.NOT_FOUND, () -> service.requireTripOwnerOrMember(10L, 1L));
        verifyNoInteractions(members);
    }

    @Test
    void missingIdentityReturnsUnauthorized() {
        assertStatus(HttpStatus.UNAUTHORIZED, () -> service.requireTripOwner(10L, null));
        assertStatus(HttpStatus.UNAUTHORIZED, () -> service.requireTripOwnerOrMember(10L, null));
        verifyNoInteractions(trips, members);
    }

    private void assertStatus(HttpStatus expected, Runnable action) {
        ResponseStatusException error = assertThrows(ResponseStatusException.class, action::run);
        assertEquals(expected, error.getStatusCode());
    }
}
