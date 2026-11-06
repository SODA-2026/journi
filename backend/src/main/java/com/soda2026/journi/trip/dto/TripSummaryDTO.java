package com.soda2026.journi.trip.dto;

import java.time.Instant;
import java.time.LocalDate;

public record TripSummaryDTO(Long tripId, String title, Instant createdAt, Instant updatedAt) {
}
