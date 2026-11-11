 package com.soda2026.journi.tripDestination;

 import com.soda2026.journi.tripDestination.dto.TripDestinationResponse;
 import org.springframework.stereotype.Component;

 @Component
 public class TripDestinationMapper {

     public TripDestinationResponse toDto(TripDestinationModel destination) {
         return new TripDestinationResponse(
                 destination.getId(),
                 destination.getTrip().getId(),
                 destination.getCity().getId(),
                 destination.getOrderIndex()
         );
     }
 }
