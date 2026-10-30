package com.soda2026.journi.trip;

import com.soda2026.journi.trip.dto.CreateTripRequest;
import com.soda2026.journi.trip.dto.TripResponse;
import org.springframework.stereotype.Component;

@Component
public class TripMapper {

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
}
