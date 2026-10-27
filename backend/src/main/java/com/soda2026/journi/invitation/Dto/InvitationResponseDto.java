package com.soda2026.journi.invitation.Dto;

import com.soda2026.journi.invitation.InvitationStatus;

import java.time.LocalDateTime;

public class InvitationResponseDto {
    private Long id;
    private Long tripId;
    private Long inviterId;
    private Long inviteeId;
    private InvitationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    public InvitationResponseDto() {
    }

    public InvitationResponseDto(Long id, Long tripId, Long inviterId, Long inviteeId, InvitationStatus status, LocalDateTime createdAt, LocalDateTime respondedAt) {
        this.id = id;
        this.tripId = tripId;
        this.inviterId = inviterId;
        this.inviteeId = inviteeId;
        this.status = status;
        this.createdAt = createdAt;
        this.respondedAt = respondedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTripId() {
        return tripId;
    }

    public Long getInviterId() {
        return inviterId;
    }

    public Long getInviteeId() {
        return inviteeId;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }
}
