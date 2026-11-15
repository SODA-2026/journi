package com.soda2026.journi.trip;

import com.soda2026.journi.trip.dto.CreateTripRequest;
import com.soda2026.journi.tripDestination.*;
import com.soda2026.journi.tripMember.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripCreationMembershipTest {
    private final TripRepository trips = mock(TripRepository.class);
    private final TripMemberRepository members = mock(TripMemberRepository.class);
    private final TripDestinationService destinations = mock(TripDestinationService.class);
    private final TripAccessService access = new TripAccessService(trips, members);
    private final TripService service = new TripService(trips,
            new TripMapper(new TripDestinationMapper()), destinations,
            mock(TripDestinationRepository.class), access);

    private CreateTripRequest request() {
        var request = new CreateTripRequest("Holiday", LocalDate.of(2027, 6, 1),
                LocalDate.of(2027, 6, 5));
        request.setCountryId(1L);
        request.setCityId(4L);
        return request;
    }

    @Test
    void creationStoresOwnerOnlyInTrip() {
        when(trips.save(any())).thenAnswer(call -> {
            TripModel trip = call.getArgument(0);
            trip.setId(10L);
            return trip;
        });

        assertEquals(10L, service.createTrip(request(), 5L).getId());

        verify(trips).save(argThat(trip -> trip.getOwnerId().equals(5L)));
        verifyNoInteractions(members);
        verify(destinations).createFirstDestination(any(TripModel.class), eq(1L), eq(4L));
    }

    @Test
    void invalidDatesDoNotCreateTrip() {
        var invalid = request();
        invalid.setEndDate(invalid.getStartDate().minusDays(1));
        assertThrows(ResponseStatusException.class, () -> service.createTrip(invalid, 5L));
        verifyNoInteractions(trips, members, destinations);
    }

    @Test
    void ownershipOfOneTripDoesNotGrantOwnershipOfAnother() {
        TripModel ownTrip = new TripModel();
        ownTrip.setId(10L);
        ownTrip.setOwnerId(5L);
        TripModel otherTrip = new TripModel();
        otherTrip.setId(20L);
        otherTrip.setOwnerId(8L);
        when(trips.findById(10L)).thenReturn(Optional.of(ownTrip));
        when(trips.findById(20L)).thenReturn(Optional.of(otherTrip));
        when(members.existsById(new TripMemberId(20L, 5L))).thenReturn(true);

        assertDoesNotThrow(() -> access.requireTripOwner(10L, 5L));
        assertDoesNotThrow(() -> access.requireTripOwnerOrMember(20L, 5L));
        assertThrows(ResponseStatusException.class, () -> access.requireTripOwner(20L, 5L));
    }
}
