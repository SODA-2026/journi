package com.soda2026.journi.tripActivity.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TripActivityRequestDTO(Long placeId, Long tripDestinationId, String name, LocalDate activityDate, LocalTime startTime, String description) {


}
