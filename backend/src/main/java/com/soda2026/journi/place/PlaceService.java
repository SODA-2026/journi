package com.soda2026.journi.place;

import com.soda2026.journi.tripDestination.TripDestinationService;
import org.springframework.stereotype.Service;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    public PlaceService(PlaceRepository placeRepository, PlaceMapper placeMapper) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
    }
}