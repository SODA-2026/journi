package com.soda2026.journi.invitation;

import com.soda2026.journi.invitation.Dto.InvitationResponseDto;
import com.soda2026.journi.tripDestination.TripDestinationMapper;
import com.soda2026.journi.tripDestination.TripDestinationModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvitationMapper {

    private final TripDestinationMapper tripDestinationMapper;

    public InvitationMapper(TripDestinationMapper tripDestinationMapper) {
        this.tripDestinationMapper = tripDestinationMapper;
    }

    public InvitationResponseDto toDto(InvitationModel invitation, List<TripDestinationModel> destinations) {
        return new InvitationResponseDto(
                invitation.getId(),
                invitation.getTrip().getId(),
                invitation.getTrip().getTitle(),
                invitation.getInviter().getName(),
                invitation.getTrip().getStartDate(),
                invitation.getTrip().getEndDate(),
                destinations.stream()
                        .map(tripDestinationMapper::toTripDestinationSummaryDTO)
                        .toList(),
                invitation.getStatus(),
                invitation.getCreatedAt(),
                invitation.getRespondedAt()
        );
    }
}
