package com.soda2026.journi.invitation;

import com.soda2026.journi.invitation.Dto.InvitationRequestDto;
import com.soda2026.journi.invitation.Dto.InvitationResponseDto;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.user.UserModel;
import org.springframework.stereotype.Component;

@Component
public class InvitationMapper {
    public InvitationResponseDto toDto(InvitationModel invitation){
        return new InvitationResponseDto(
                invitation.getId(),
                invitation.getTrip().getId(),
                invitation.getInviter().getId(),
                invitation.getInvitee().getId(),
                invitation.getStatus(),
                invitation.getCreatedAt(),
                invitation.getRespondedAt()
        );
    }

    public InvitationModel toEntity(TripModel trip, UserModel inviter, UserModel invitee){
        return new InvitationModel(trip, inviter, invitee);

    }
}
