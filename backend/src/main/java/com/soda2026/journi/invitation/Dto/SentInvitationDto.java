package com.soda2026.journi.invitation.Dto;

import com.soda2026.journi.invitation.InvitationStatus;

public record SentInvitationDto(
        Long id,
        Long tripId,
        String tripTitle,
        String inviteeEmail,
        InvitationStatus status
) {}
