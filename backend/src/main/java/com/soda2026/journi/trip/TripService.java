package com.soda2026.journi.trip;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    public TripService(TripRepository tripRepository, TripMapper tripMapper) {
        this.tripRepository = tripRepository;
        this.tripMapper = tripMapper;
    }

    public List<TripResponseDTO> getAll() {
        return tripRepository.findAll().stream()
                .map(tripMapper::toDto)
                .toList();
    }
}
