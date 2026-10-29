package com.soda2026.journi.trip;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateTripRequest(
        @NotBlank @Size(max = 100) String country,
        @NotBlank @Size(max = 150) String city,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {
    public CreateTripRequest {
        country = country == null ? null : country.strip();
        city = city == null ? null : city.strip();
    }

    @AssertTrue(message = "Slutdatum får inte vara före startdatum")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
