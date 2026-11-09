package com.soda2026.journi.city;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public CityService(CityRepository cityRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    public List<CityResponseDTO> getAllByCountry(Long countryId) {
        return cityRepository.findAllByCountryId(countryId)
                .stream()
                .map(cityMapper::toDto)
                .toList();
    }
}
