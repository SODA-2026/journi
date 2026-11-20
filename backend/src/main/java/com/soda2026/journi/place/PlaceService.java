package com.soda2026.journi.place;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    public PlaceService(PlaceRepository placeRepository, PlaceMapper placeMapper) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
    }

    public List<PlaceResponseDTO> getPlaces() {
        return placeRepository.findAll()
                .stream()
                .map(placeMapper::toDto)
                .toList();
    }
}