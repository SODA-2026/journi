package com.soda2026.journi.tripDestination;

import com.soda2026.journi.tripDestination.dto.TripDestinationResponse;
import org.springframework.stereotype.Component;

@Component
public class TripDestinationMapper {

    public TripDestinationResponse toDto(TripDestinationModel destination) {
        return new TripDestinationResponse(
                destination.getId(),
                destination.getTrip().getId(),
                destination.getSourceSuggestion() == null
                        ? null
                        : destination.getSourceSuggestion().getId(),
                destination.getCountry(),
                destination.getPlaceName(),
                destination.getOrderIndex()
        );
    }
}
