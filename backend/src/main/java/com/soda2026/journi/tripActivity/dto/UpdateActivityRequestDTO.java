package com.soda2026.journi.tripActivity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateActivityRequestDTO(
        @NotBlank(message = "Namn är obligatoriskt")
        String name,

        @NotNull(message = "Datum är obligatoriskt")
        LocalDate activityDate,
        LocalTime startTime,
        String description
) {
}
