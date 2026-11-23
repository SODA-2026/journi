package com.soda2026.journi.invitation.Dto;

import com.soda2026.journi.invitation.InvitationStatus;
import com.soda2026.journi.tripDestination.dto.TripDestinationSummaryDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InvitationResponseDto(
        Long id,
        Long tripId,
        String tripTitle,
        String inviterName,
        LocalDate startDate,
        LocalDate endDate,
        List<TripDestinationSummaryDTO> destinations,
        InvitationStatus status,
        LocalDateTime createdAt,
        LocalDateTime respondedAt
) {}
