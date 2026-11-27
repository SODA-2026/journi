package com.soda2026.journi.city;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public CityService(CityRepository cityRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    public List<CityResponseDTO> getAll() {
        return cityRepository.findAll(Sort.by("name"))
                .stream()
                .map(cityMapper::toDto)
                .toList();
    }

    public List<CityResponseDTO> getAllByCountry(Long countryId) {
        return cityRepository.findAllByCountryId(countryId, Sort.by("name"))
                .stream()
                .map(cityMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CityResponseDTO getCity(Long cityId){
        return cityMapper.toDto(cityRepository.findById(cityId)
                .orElseThrow(() -> new RuntimeException("Staden hittades inte")));

    }
}
