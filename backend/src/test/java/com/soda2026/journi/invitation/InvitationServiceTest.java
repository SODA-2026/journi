package com.soda2026.journi.invitation;

import com.soda2026.journi.invitation.Dto.InvitationRequestDto;
import com.soda2026.journi.trip.TripAccessService;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.trip.TripRepository;
import com.soda2026.journi.tripDestination.TripDestinationMapper;
import com.soda2026.journi.tripDestination.TripDestinationModel;
import com.soda2026.journi.tripDestination.TripDestinationRepository;
import com.soda2026.journi.tripMember.TripMemberId;
import com.soda2026.journi.tripMember.TripMemberRepository;
import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class InvitationServiceTest {

    private final InvitationRepository invitationRepository = mock(InvitationRepository.class);
    private final TripRepository tripRepository = mock(TripRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final TripMemberRepository tripMemberRepository = mock(TripMemberRepository.class);
    private final TripDestinationRepository tripDestinationRepository = mock(TripDestinationRepository.class);
    private final TripAccessService tripAccessService = new TripAccessService(tripRepository, tripMemberRepository);
    private final InvitationService invitationService = new InvitationService(
            invitationRepository,
            new InvitationMapper(new TripDestinationMapper()),
            tripRepository,
            userRepository,
            tripMemberRepository,
            tripAccessService,
            tripDestinationRepository
    );
    private final TripModel trip = new TripModel();
    private final UserModel owner = new UserModel("owner@example.com", "Owner", "hash", null);
    private final UserModel recipient = new UserModel("guest@example.com", "Guest", "hash", null);
    private InvitationModel invitation;

    @BeforeEach
    void setup() {
        trip.setId(10L);
        trip.setOwnerId(1L);
        trip.setTitle("Paris");
        owner.setId(1L);
        recipient.setId(2L);
        invitation = new InvitationModel(trip, owner, recipient);

        when(tripRepository.findById(10L)).thenReturn(Optional.of(trip));
        when(tripRepository.findForUpdate(10L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(userRepository.findByEmail("guest@example.com")).thenReturn(Optional.of(recipient));
        when(invitationRepository.save(any())).thenAnswer(call -> call.getArgument(0));
        when(invitationRepository.findForUpdate(20L)).thenReturn(Optional.of(invitation));
    }

    @Test
    void incomingIncludesTripPreviewBeforeAcceptanceWithoutGrantingAccess() {
        trip.setStartDate(LocalDate.of(2026, 12, 1));
        trip.setEndDate(LocalDate.of(2026, 12, 5));
        TripDestinationModel destination = mock(TripDestinationModel.class, RETURNS_DEEP_STUBS);
        when(destination.getId()).thenReturn(30L);
        when(destination.getCity().getName()).thenReturn("Paris");
        when(destination.getCity().getCountry().getName()).thenReturn("France");
        when(tripDestinationRepository.findAllByTrip_IdOrderByOrderIndexAsc(10L)).thenReturn(List.of(destination));
        when(invitationRepository.findAllByInvitee_IdAndStatusOrderByCreatedAtDesc(2L, InvitationStatus.PENDING))
                .thenReturn(List.of(invitation));

        var preview = invitationService.getIncomingInvitations(2L).getFirst();

        assertEquals("Paris", preview.tripTitle());
        assertEquals(trip.getStartDate(), preview.startDate());
        assertEquals(trip.getEndDate(), preview.endDate());
        assertEquals("Paris", preview.destinations().getFirst().city());
        assertEquals("France", preview.destinations().getFirst().country());
        assertEquals(InvitationStatus.PENDING, preview.status());
        verify(tripMemberRepository, never()).save(any());
        assertStatus(HttpStatus.FORBIDDEN,
                () -> tripAccessService.requireTripOwnerOrMember(10L, 2L));
    }

    @Test
    void ownerInvitesRegisteredUserWithoutGrantingAccess() {
        var result = invitationService.sendInvitation(new InvitationRequestDto(10L, " guest@example.com "), 1L);

        assertEquals(InvitationStatus.PENDING, result.status());
        assertEquals("Paris", result.tripTitle());
        verify(tripMemberRepository, never()).save(any());
    }

    @Test
    void memberCannotInvite() {
        assertStatus(HttpStatus.FORBIDDEN,
                () -> invitationService.sendInvitation(new InvitationRequestDto(10L, "guest@example.com"), 2L));
        verifyNoInteractions(invitationRepository, userRepository);
    }

    @Test
    void cannotInviteSelfOrExistingMember() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        assertStatus(HttpStatus.BAD_REQUEST,
                () -> invitationService.sendInvitation(new InvitationRequestDto(10L, "owner@example.com"), 1L));
        when(tripMemberRepository.existsById(new TripMemberId(10L, 2L))).thenReturn(true);
        assertStatus(HttpStatus.CONFLICT,
                () -> invitationService.sendInvitation(new InvitationRequestDto(10L, "guest@example.com"), 1L));
        verify(invitationRepository, never()).save(any());
    }

    @Test
    void unknownEmailReturnsNotFound() {
        assertStatus(HttpStatus.NOT_FOUND,
                () -> invitationService.sendInvitation(new InvitationRequestDto(10L, "unknown@example.com"), 1L));
    }

    @Test
    void pendingInvitationCannotBeDuplicated() {
        when(invitationRepository.findByTrip_IdAndInvitee_Id(10L, 2L)).thenReturn(Optional.of(invitation));
        assertStatus(HttpStatus.CONFLICT,
                () -> invitationService.sendInvitation(new InvitationRequestDto(10L, "guest@example.com"), 1L));
    }

    @Test
    void declinedInvitationCanBeSentAgain() {
        invitation.setStatus(InvitationStatus.DECLINED);
        invitation.setRespondedAt(LocalDateTime.now());
        when(invitationRepository.findByTrip_IdAndInvitee_Id(10L, 2L)).thenReturn(Optional.of(invitation));
        var result = invitationService.sendInvitation(new InvitationRequestDto(10L, "guest@example.com"), 1L);

        assertEquals(InvitationStatus.PENDING, result.status());
        assertNull(result.respondedAt());
    }

    @Test
    void removedMemberCanBeInvitedAgainButMustAcceptAgain() {
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());
        when(invitationRepository.findByTrip_IdAndInvitee_Id(10L, 2L)).thenReturn(Optional.of(invitation));

        var result = invitationService.sendInvitation(new InvitationRequestDto(10L, "guest@example.com"), 1L);

        assertEquals(InvitationStatus.PENDING, result.status());
        assertNull(result.respondedAt());
        verify(tripMemberRepository, never()).save(any());
        assertStatus(HttpStatus.FORBIDDEN,
                () -> tripAccessService.requireTripOwnerOrMember(10L, 2L));
    }

    @Test
    void acceptingGrantsTripAccessAndCannotBeRepeated() {
        invitationService.respondToInvitation(20L, 2L, true);
        verify(tripMemberRepository).save(argThat(member ->
                member.getId().equals(new TripMemberId(10L, 2L))));
        assertEquals(InvitationStatus.ACCEPTED, invitation.getStatus());
        assertNotNull(invitation.getRespondedAt());
        when(tripMemberRepository.existsById(new TripMemberId(10L, 2L))).thenReturn(true);
        assertDoesNotThrow(() -> tripAccessService.requireTripOwnerOrMember(10L, 2L));
        assertStatus(HttpStatus.CONFLICT,
                () -> invitationService.respondToInvitation(20L, 2L, true));
        verify(tripMemberRepository, times(1)).save(any());
    }

    @Test
    void decliningDoesNotGrantAccess() {
        invitationService.respondToInvitation(20L, 2L, false);
        assertEquals(InvitationStatus.DECLINED, invitation.getStatus());
        verify(tripMemberRepository, never()).save(any());
        assertStatus(HttpStatus.FORBIDDEN,
                () -> tripAccessService.requireTripOwnerOrMember(10L, 2L));
    }

    @Test
    void otherUsersCannotAnswerEvenIfTheyOwnTheTrip() {
        assertStatus(HttpStatus.FORBIDDEN,
                () -> invitationService.respondToInvitation(20L, 1L, true));
        assertStatus(HttpStatus.FORBIDDEN,
                () -> invitationService.respondToInvitation(20L, 3L, false));
        assertEquals(InvitationStatus.PENDING, invitation.getStatus());
        verifyNoInteractions(tripMemberRepository);
    }

    @Test
    void missingInvitationReturnsNotFound() {
        assertStatus(HttpStatus.NOT_FOUND,
                () -> invitationService.respondToInvitation(99L, 2L, true));
    }

    private void assertStatus(HttpStatus expected, Runnable action) {
        ResponseStatusException error = assertThrows(ResponseStatusException.class, action::run);
        assertEquals(expected, error.getStatusCode());
    }
}
