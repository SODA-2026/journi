 package com.soda2026.journi.tripActivity.dto;

 import java.time.LocalDate;
 import java.time.LocalTime;
 import com.soda2026.journi.tripActivity.TripActivityStatus;

 public record TripActivityResponseDTO(
         Long id,
         Long tripDestinationId,
         String name,
         LocalDate activityDate,
         LocalTime startTime,
         String description,
         TripActivityStatus status
         ) {}
