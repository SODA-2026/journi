package com.soda2026.journi.trip.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateTripRequestDTO(@NotBlank @Size(max = 150) String title, @NotNull LocalDate startDate, @NotNull LocalDate endDate) {

}
