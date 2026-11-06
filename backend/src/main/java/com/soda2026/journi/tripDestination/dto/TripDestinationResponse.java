 package com.soda2026.journi.tripDestination.dto;

 public record TripDestinationResponse(
         Long id,
         Long tripId,
         Long cityId,
         Integer orderIndex
 ) {
 }
