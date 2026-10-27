package com.soda2026.journi.activity;

import org.antlr.v4.runtime.misc.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record ActivityResponseDTO(
        Long id,
        Long tripDestinationId,
        String name,
        LocalDate activityDate,
        LocalTime startTime,
        String description,
        ActivityStatus status,
        Instant createdAt,
        Instant updatedAt) {}