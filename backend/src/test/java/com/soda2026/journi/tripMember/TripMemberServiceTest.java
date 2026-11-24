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
        UserModel owner = new UserModel();
        owner.setId(1L);
        owner.setName("Olena");
        when(entities.find(UserModel.class, 1L)).thenReturn(owner);
        return trip;
    }

    @Test
    void returnsInvitedMembersWithoutStoredRoles() {
        TripModel trip = setupTrip();
        UserModel member = new UserModel();
        member.setId(2L);
        member.setName("Sam");
        when(repository.findAllById_TripIdOrderById_UserIdAsc(10L)).thenReturn(List.of(
                new TripMemberModel(trip, member)));

        var result = service.getMembers(10L, 1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().tripId());
        assertEquals(2L, result.getFirst().userId());
        assertEquals("Sam", result.getFirst().name());
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
        user.setName("Sam");
        TripMemberModel member = new TripMemberModel(trip, user);
        when(repository.findById(new TripMemberId(10L, 2L))).thenReturn(Optional.of(member));

        service.removeMember(10L, 2L, 1L);

        verify(access).requireTripOwner(10L, 1L);
        verify(repository).delete(member);
        verify(entities).persist(argThat(value -> value instanceof TripMemberEventModel event
                && event.getEventType() == TripMemberEventType.MEMBER_REMOVED
                && event.getActorId().equals(1L)
                && event.getActorName().equals("Olena")
                && event.getMemberId().equals(2L)
                && event.getMemberName().equals("Sam")));
    }

    @Test
    void deniedAccessPreventsDeletion() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN))
                .when(access).requireTripOwner(10L, 1L);
        assertThrows(ResponseStatusException.class,
                () -> service.removeMember(10L, 2L, 1L));
        verifyNoInteractions(repository);
        verify(entities, never()).persist(any());
    }

    @Test
    void eventFailurePreventsMemberRemoval() {
        TripModel trip = setupTrip();
        UserModel user = new UserModel();
        user.setId(2L);
        when(repository.findById(new TripMemberId(10L, 2L)))
                .thenReturn(Optional.of(new TripMemberModel(trip, user)));
        doThrow(new IllegalStateException("Cannot store event")).when(entities).persist(any());

        assertThrows(IllegalStateException.class, () -> service.removeMember(10L, 2L, 1L));
        verify(repository, never()).delete(any());
    }

    @Test
    void removedMemberLosesAccessWhileOwnerAndOtherMembersKeepAccess() {
        TripModel trip = setupTrip();
        var trips = mock(com.soda2026.journi.trip.TripRepository.class);
        when(trips.findById(10L)).thenReturn(Optional.of(trip));
        var realAccess = new TripAccessService(trips, repository);
        var memberService = new TripMemberService(repository, entities, realAccess);
        UserModel user = new UserModel();
        user.setId(2L);
        TripMemberModel member = new TripMemberModel(trip, user);
        when(repository.findById(new TripMemberId(10L, 2L))).thenReturn(Optional.of(member));
        when(repository.existsById(new TripMemberId(10L, 2L))).thenReturn(true);
        when(repository.existsById(new TripMemberId(10L, 3L))).thenReturn(true);
        doAnswer(invocation -> {
            when(repository.existsById(new TripMemberId(10L, 2L))).thenReturn(false);
            return null;
        }).when(repository).delete(member);

        assertDoesNotThrow(() -> realAccess.requireTripOwnerOrMember(10L, 2L));
        memberService.removeMember(10L, 2L, 1L);

        var error = assertThrows(ResponseStatusException.class,
                () -> realAccess.requireTripOwnerOrMember(10L, 2L));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        assertDoesNotThrow(() -> realAccess.requireTripOwnerOrMember(10L, 1L));
        assertDoesNotThrow(() -> realAccess.requireTripOwnerOrMember(10L, 3L));
        verify(trips, never()).delete(any());
    }
}
