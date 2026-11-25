package com.soda2026.journi.trip.dto;

import java.time.LocalDate;

public record UpdateTripResponseDTO(String title, LocalDate startDate, LocalDate endDate) {
}
