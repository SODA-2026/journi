package com.soda2026.journi.tripMember;

import com.soda2026.journi.trip.TripAccessService;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.user.UserModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripMemberServiceTest {
    private final TripMemberRepository repository = mock(TripMemberRepository.class);
    private final EntityManager entities = mock(EntityManager.class);
    private final TripAccessService access = mock(TripAccessService.class);
    private final TripMemberService service = new TripMemberService(repository, entities, access);

    private TripModel setupTrip() {
        TripModel trip = new TripModel();
        trip.setId(10L);
        trip.setOwnerId(1L);
        when(entities.find(TripModel.class, 10L)).thenReturn(trip);
        return trip;
    }

    @Test
    void returnsInvitedMembersWithoutStoredRoles() {
        TripModel trip = setupTrip();
        UserModel member = new UserModel();
        member.setId(2L);
        when(repository.findAllById_TripIdOrderById_UserIdAsc(10L)).thenReturn(List.of(
                new TripMemberModel(trip, member)));

        var result = service.getMembers(10L, 1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().tripId());
        assertEquals(2L, result.getFirst().userId());
        verify(access).requireTripOwnerOrMember(10L, 1L);
    }

    @Test
    void preventsRemovingTripOwner() {
        setupTrip();
        var error = assertThrows(ResponseStatusException.class,
                () -> service.removeMember(10L, 1L, 1L));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void ownerCanRemoveMember() {
        TripModel trip = setupTrip();
        UserModel user = new UserModel();
        user.setId(2L);
        TripMemberModel member = new TripMemberModel(trip, user);
        when(repository.findById(new TripMemberId(10L, 2L))).thenReturn(Optional.of(member));

        service.removeMember(10L, 2L, 1L);

        verify(access).requireTripOwner(10L, 1L);
        verify(repository).delete(member);
    }

    @Test
    void deniedAccessPreventsDeletion() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN))
                .when(access).requireTripOwner(10L, 1L);
        assertThrows(ResponseStatusException.class,
                () -> service.removeMember(10L, 2L, 1L));
        verifyNoInteractions(repository);
    }
}
