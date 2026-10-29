package com.soda2026.journi.trip;

import java.time.LocalDate;

public record CreatedTripResponse(
        Long id, String country, String city, LocalDate startDate, LocalDate endDate
) {}
