 package com.soda2026.journi.tripDestination.dto;

 import jakarta.validation.constraints.NotNull;
 import jakarta.validation.constraints.Positive;
 import jakarta.validation.constraints.PositiveOrZero;

 public record CreateTripDestinationRequest(
         @NotNull
         @Positive
         Long cityId,

         @NotNull
         @PositiveOrZero
         Integer orderIndex
 ) {
 }
