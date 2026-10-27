package com.soda2026.journi.trip;

import org.springframework.stereotype.Component;

@Component
public class TripMapper {

    public TripResponseDTO toDto(TripModel trip){
        return new TripResponseDTO(
                trip.getTitle()
        );
    }
}
