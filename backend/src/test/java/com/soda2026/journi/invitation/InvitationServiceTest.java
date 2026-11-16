package com.soda2026.journi.invitation;

import com.soda2026.journi.invitation.Dto.InvitationRequestDto;
import com.soda2026.journi.trip.*;
import com.soda2026.journi.tripDestination.*;
import com.soda2026.journi.tripMember.*;
import com.soda2026.journi.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvitationServiceTest {
    final InvitationRepository invitations = mock(InvitationRepository.class);
    final TripRepository trips = mock(TripRepository.class);
    final UserRepository users = mock(UserRepository.class);
    final TripMemberRepository members = mock(TripMemberRepository.class);
    final TripDestinationRepository destinations = mock(TripDestinationRepository.class);
    final TripAccessService access = new TripAccessService(trips, members);
    final InvitationService service = new InvitationService(invitations, new InvitationMapper(new TripDestinationMapper()), trips, users, members, access, destinations);
    final TripModel trip = new TripModel();
    final UserModel owner = new UserModel("owner@example.com", "Owner", "hash", null);
    final UserModel recipient = new UserModel("guest@example.com", "Guest", "hash", null);
    InvitationModel invitation;

    @BeforeEach void setup() {
        trip.setId(10L); trip.setOwnerId(1L); trip.setTitle("Paris");
        owner.setId(1L); recipient.setId(2L);
        invitation = new InvitationModel(trip, owner, recipient);
        when(trips.findById(10L)).thenReturn(Optional.of(trip));
        when(trips.findForUpdate(10L)).thenReturn(Optional.of(trip));
        when(users.findById(1L)).thenReturn(Optional.of(owner));
        when(users.findByEmail("guest@example.com")).thenReturn(Optional.of(recipient));
        when(invitations.save(any())).thenAnswer(call -> call.getArgument(0));
        when(invitations.findForUpdate(20L)).thenReturn(Optional.of(invitation));
    }

    @Test void incomingIncludesTripPreviewBeforeAcceptanceWithoutGrantingAccess() {
        trip.setStartDate(java.time.LocalDate.of(2026, 12, 1));
        trip.setEndDate(java.time.LocalDate.of(2026, 12, 5));
        TripDestinationModel destination = mock(TripDestinationModel.class, RETURNS_DEEP_STUBS);
        when(destination.getId()).thenReturn(30L);
        when(destination.getCity().getName()).thenReturn("Paris");
        when(destination.getCity().getCountry().getName()).thenReturn("France");
        when(destinations.findAllByTrip_IdOrderByOrderIndexAsc(10L)).thenReturn(java.util.List.of(destination));
        when(invitations.findAllByInvitee_IdAndStatusOrderByCreatedAtDesc(2L, InvitationStatus.PENDING))
                .thenReturn(java.util.List.of(invitation));

        var preview = service.incoming(2L).getFirst();

        assertEquals("Paris", preview.tripTitle());
        assertEquals(trip.getStartDate(), preview.startDate());
        assertEquals(trip.getEndDate(), preview.endDate());
        assertEquals("Paris", preview.destinations().getFirst().city());
        assertEquals("France", preview.destinations().getFirst().country());
        assertEquals(InvitationStatus.PENDING, preview.status());
        verify(members, never()).save(any());
        status(HttpStatus.FORBIDDEN, () -> access.requireTripOwnerOrMember(10L, 2L));
    }

    @Test void ownerInvitesRegisteredUserWithoutGrantingAccess() {
        var result = service.invite(new InvitationRequestDto(10L, " guest@example.com "), 1L);
        assertEquals(InvitationStatus.PENDING, result.status());
        assertEquals("Paris", result.tripTitle());
        verify(members, never()).save(any());
    }

    @Test void memberCannotInvite() {
        status(HttpStatus.FORBIDDEN, () -> service.invite(new InvitationRequestDto(10L, "guest@example.com"), 2L));
        verifyNoInteractions(invitations, users);
    }

    @Test void cannotInviteSelfOrExistingMember() {
        when(users.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        status(HttpStatus.BAD_REQUEST, () -> service.invite(new InvitationRequestDto(10L, "owner@example.com"), 1L));
        when(members.existsById(new TripMemberId(10L, 2L))).thenReturn(true);
        status(HttpStatus.CONFLICT, () -> service.invite(new InvitationRequestDto(10L, "guest@example.com"), 1L));
        verify(invitations, never()).save(any());
    }

    @Test void unknownEmailReturnsNotFound() {
        status(HttpStatus.NOT_FOUND, () -> service.invite(new InvitationRequestDto(10L, "unknown@example.com"), 1L));
    }

    @Test void pendingInvitationCannotBeDuplicated() {
        when(invitations.findByTrip_IdAndInvitee_Id(10L, 2L)).thenReturn(Optional.of(invitation));
        status(HttpStatus.CONFLICT, () -> service.invite(new InvitationRequestDto(10L, "guest@example.com"), 1L));
    }

    @Test void declinedInvitationCanBeSentAgain() {
        invitation.setStatus(InvitationStatus.DECLINED);
        invitation.setRespondedAt(java.time.LocalDateTime.now());
        when(invitations.findByTrip_IdAndInvitee_Id(10L, 2L)).thenReturn(Optional.of(invitation));
        var result = service.invite(new InvitationRequestDto(10L, "guest@example.com"), 1L);
        assertEquals(InvitationStatus.PENDING, result.status());
        assertNull(result.respondedAt());
    }

    @Test void acceptingGrantsTripAccessAndCannotBeRepeated() {
        service.respond(20L, 2L, true);
        verify(members).save(argThat(member -> member.getId().equals(new TripMemberId(10L, 2L))));
        assertEquals(InvitationStatus.ACCEPTED, invitation.getStatus());
        assertNotNull(invitation.getRespondedAt());
        when(members.existsById(new TripMemberId(10L, 2L))).thenReturn(true);
        assertDoesNotThrow(() -> access.requireTripOwnerOrMember(10L, 2L));
        status(HttpStatus.CONFLICT, () -> service.respond(20L, 2L, true));
        verify(members, times(1)).save(any());
    }

    @Test void decliningDoesNotGrantAccess() {
        service.respond(20L, 2L, false);
        assertEquals(InvitationStatus.DECLINED, invitation.getStatus());
        verify(members, never()).save(any());
        status(HttpStatus.FORBIDDEN, () -> access.requireTripOwnerOrMember(10L, 2L));
    }

    @Test void otherUsersCannotAnswerEvenIfTheyOwnTheTrip() {
        status(HttpStatus.FORBIDDEN, () -> service.respond(20L, 1L, true));
        status(HttpStatus.FORBIDDEN, () -> service.respond(20L, 3L, false));
        assertEquals(InvitationStatus.PENDING, invitation.getStatus());
        verifyNoInteractions(members);
    }

    @Test void missingInvitationReturnsNotFound() {
        status(HttpStatus.NOT_FOUND, () -> service.respond(99L, 2L, true));
    }

    private void status(HttpStatus expected, Runnable action) {
        assertEquals(expected, assertThrows(ResponseStatusException.class, action::run).getStatusCode());
    }
}
