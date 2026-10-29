package com.soda2026.journi.trip;

import org.springframework.stereotype.Component;

@Component
public class TripMapper {

    public TripResponseDTO toDto(TripModel trip){
        return new TripResponseDTO(
                trip.getTitle()
        );
    }

    public TripModel toEntity(CreateTripRequestDTO createTripRequestDTO) {

        if (createTripRequestDTO == null) {
            return null;
        }

        TripModel trip = new TripModel();

        trip.setTitle(createTripRequestDTO.getTitle());
        trip.setStartDate(createTripRequestDTO.getStartDate());
        trip.setEndDate(createTripRequestDTO.getEndDate());

        return trip;
    }
}
