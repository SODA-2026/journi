package com.soda2026.journi.trip.dto;

import java.time.Instant;
import java.time.LocalDate;

public record TripSummaryDTO(
        Long tripId,
        Long ownerId,
        String ownerName,
        String title,
        Instant createdAt,
        LocalDate startDate
) {}
