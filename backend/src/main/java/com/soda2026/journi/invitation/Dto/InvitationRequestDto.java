package com.soda2026.journi.invitation.Dto;

import jakarta.validation.constraints.NotNull;

public class InvitationRequestDto {
    @NotNull private Long tripId;
    @NotNull private Long inviterId;
    @NotNull private Long inviteeId;

    public InvitationRequestDto() {
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getInviterId() {
        return inviterId;
    }

    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }

    public Long getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(Long inviteeId) {
        this.inviteeId = inviteeId;
    }
}
