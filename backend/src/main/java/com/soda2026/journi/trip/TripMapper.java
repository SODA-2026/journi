 package com.soda2026.journi.trip;

 import com.soda2026.journi.trip.dto.CreateTripRequest;
 import com.soda2026.journi.trip.dto.TripInfoDTO;
 import com.soda2026.journi.trip.dto.TripResponse;
 import com.soda2026.journi.trip.dto.TripSummaryDTO;
 import com.soda2026.journi.tripDestination.TripDestinationMapper;
 import com.soda2026.journi.tripDestination.TripDestinationModel;
 import com.soda2026.journi.tripDestination.TripDestinationRepository;
 import com.soda2026.journi.tripDestination.dto.TripDestinationSummaryDTO;
 import org.springframework.stereotype.Component;

 import java.util.ArrayList;
 import java.util.List;

 @Component
 public class TripMapper {
     private final TripDestinationMapper tripDestinationMapper;

     public TripMapper(TripDestinationMapper tripDestinationMapper) {
         this.tripDestinationMapper = tripDestinationMapper;
     }

     public TripResponse toDto(TripModel trip){
         return new TripResponse(
                 trip.getId(),
                 trip.getTitle()
         );
     }

     public TripModel toEntity(CreateTripRequest createTripRequest) {

         if (createTripRequest == null) {
             return null;
         }

         TripModel trip = new TripModel();

         trip.setTitle(createTripRequest.getTitle());
         trip.setStartDate(createTripRequest.getStartDate());
         trip.setEndDate(createTripRequest.getEndDate());

         return trip;
     }

     public TripSummaryDTO toTripSummaryDTO(TripModel trip){
         return new TripSummaryDTO(
                 trip.getId(),
                 trip.getTitle(),
                 trip.getCreatedAt(),
                 trip.getStartDate()
         );
     }

     public TripInfoDTO toTripInfoDTO(TripModel tripModel, List<TripDestinationModel> destinations){
         return new TripInfoDTO(
                 tripModel.getId(),
                 tripModel.getTitle(),
                 tripModel.getStartDate(),
                 tripModel.getEndDate(),
                 tripModel.getCreatedAt(),
                 destinations.stream()
                         .map(tripDestinationMapper::toTripDestinationSummaryDTO)
                         .toList()
         );
     }


 }
