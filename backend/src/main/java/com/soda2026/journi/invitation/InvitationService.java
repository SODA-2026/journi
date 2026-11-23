package com.soda2026.journi.invitation;

import com.soda2026.journi.invitation.Dto.InvitationRequestDto;
import com.soda2026.journi.invitation.Dto.InvitationResponseDto;
import com.soda2026.journi.invitation.Dto.SentInvitationDto;
import com.soda2026.journi.trip.TripAccessService;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.trip.TripRepository;
import com.soda2026.journi.tripDestination.TripDestinationModel;
import com.soda2026.journi.tripDestination.TripDestinationRepository;
import com.soda2026.journi.tripMember.TripMemberId;
import com.soda2026.journi.tripMember.TripMemberModel;
import com.soda2026.journi.tripMember.TripMemberRepository;
import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripMemberRepository tripMemberRepository;
    private final TripAccessService tripAccessService;
    private final TripDestinationRepository tripDestinationRepository;

    public InvitationService(
            InvitationRepository invitationRepository,
            InvitationMapper invitationMapper,
            TripRepository tripRepository,
            UserRepository userRepository,
            TripMemberRepository tripMemberRepository,
            TripAccessService tripAccessService,
            TripDestinationRepository tripDestinationRepository
    ) {
        this.invitationRepository = invitationRepository;
        this.invitationMapper = invitationMapper;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.tripMemberRepository = tripMemberRepository;
        this.tripAccessService = tripAccessService;
        this.tripDestinationRepository = tripDestinationRepository;
    }

    // After waiting for the trip lock, duplicate checks must see the latest committed invitations.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InvitationResponseDto sendInvitation(InvitationRequestDto invitationRequest, Long userId) {
        tripAccessService.requireTripOwner(invitationRequest.tripId(), userId);

        // Serialize invitations for a trip, including duplicate requests from different tabs.
        TripModel trip = tripRepository.findForUpdate(invitationRequest.tripId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Trip not found"));

        UserModel invitee = userRepository.findByEmail(invitationRequest.email().trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Registered user not found"));

        if (invitee.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot invite yourself");
        }

        if (tripAccessService.isMember(trip.getId(), invitee.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already a member");
        }

        InvitationModel invitation = invitationRepository
                .findByTrip_IdAndInvitee_Id(trip.getId(), invitee.getId())
                .orElse(null);

        if (invitation != null) {
            if (invitation.getStatus() == InvitationStatus.PENDING) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User has already been invited");
            }

            invitationRepository.delete(invitation);
            // Free the unique trip and invitee pair before inserting a new invitation.
            invitationRepository.flush();
        }

        UserModel inviter = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
        return toDto(invitationRepository.save(new InvitationModel(trip, inviter, invitee)));
    }

    public List<InvitationResponseDto> getIncomingInvitations(Long userId) {
        return invitationRepository
                .findAllByInvitee_IdAndStatusOrderByCreatedAtDesc(userId, InvitationStatus.PENDING)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<SentInvitationDto> getSentInvitations(Long userId) {
        return invitationRepository.findSentByUserId(userId);
    }

    @Transactional
    public InvitationResponseDto respondToInvitation(Long invitationId, Long userId, boolean accept) {
        InvitationModel invitation = invitationRepository.findForUpdate(invitationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Invitation not found"));

        if (!invitation.getInvitee().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the recipient may respond");
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invitation has already been answered");
        }

        if (accept) {
            TripMemberId memberId = new TripMemberId(invitation.getTrip().getId(), userId);
            if (!tripMemberRepository.existsById(memberId)) {
                tripMemberRepository.save(new TripMemberModel(invitation.getTrip(), invitation.getInvitee()));
            }
        }

        invitation.setStatus(accept ? InvitationStatus.ACCEPTED : InvitationStatus.DECLINED);
        invitation.setRespondedAt(LocalDateTime.now());

        return toDto(invitation);
    }

    private InvitationResponseDto toDto(InvitationModel invitation) {
        Long tripId = invitation.getTrip().getId();
        List<TripDestinationModel> destinations = tripDestinationRepository
                .findAllByTrip_IdOrderByOrderIndexAsc(tripId);
        return invitationMapper.toDto(invitation, destinations);
    }
}
