package com.soda2026.journi.tripMember;

import com.soda2026.journi.trip.TripAccessService;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.user.UserModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripMemberServiceTest {
    private final TripMemberRepository repository = mock(TripMemberRepository.class);
    private final EntityManager entities = mock(EntityManager.class);
    private final TripAccessService access = mock(TripAccessService.class);
    private final TripMemberService service = new TripMemberService(repository, entities, access);

    private TripModel setupOwner() {
        UserModel owner = new UserModel();
        owner.setId(1L);
        @SuppressWarnings("unchecked")
        TypedQuery<UserModel> query = mock(TypedQuery.class);
        when(entities.createQuery(anyString(), eq(UserModel.class))).thenReturn(query);
        when(query.setParameter("email", "owner@example.com")).thenReturn(query);
        when(query.getResultStream()).thenAnswer(call -> Stream.of(owner));
        TripModel trip = new TripModel();
        trip.setId(10L);
        trip.setOwnerId(1L);
        when(entities.find(TripModel.class, 10L)).thenReturn(trip);
        return trip;
    }

    @Test
    void returnsInvitedMembersWithoutStoredRoles() {
        TripModel trip = setupOwner();
        UserModel member = new UserModel();
        member.setId(2L);
        when(repository.findAllById_TripIdOrderById_UserIdAsc(10L)).thenReturn(List.of(
                new TripMemberModel(trip, member)));

        var result = service.getMembers(10L, () -> "owner@example.com");

        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().tripId());
        assertEquals(2L, result.getFirst().userId());
        verify(access).requireTripOwnerOrMember(10L, 1L);
    }

    @Test
    void preventsRemovingTripOwner() {
        setupOwner();
        var error = assertThrows(ResponseStatusException.class,
                () -> service.removeMember(10L, 1L, () -> "owner@example.com"));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void ownerCanRemoveMember() {
        TripModel trip = setupOwner();
        UserModel user = new UserModel();
        user.setId(2L);
        TripMemberModel member = new TripMemberModel(trip, user);
        when(repository.findById(new TripMemberId(10L, 2L))).thenReturn(Optional.of(member));

        service.removeMember(10L, 2L, () -> "owner@example.com");

        verify(access).requireTripOwner(10L, 1L);
        verify(repository).delete(member);
    }

    @Test
    void deniedAccessPreventsDeletion() {
        setupOwner();
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN))
                .when(access).requireTripOwner(10L, 1L);
        assertThrows(ResponseStatusException.class,
                () -> service.removeMember(10L, 2L, () -> "owner@example.com"));
        verifyNoInteractions(repository);
    }
}
