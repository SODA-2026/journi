package com.soda2026.journi.trip.dto;


import com.soda2026.journi.tripDestination.dto.TripDestinationSummaryDTO;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record TripInfoDTO(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        Instant createdAt,
        List<TripDestinationSummaryDTO> destinations
        ) {}
