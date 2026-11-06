package com.soda2026.journi.country;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public CountryService(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    public List<CountryResponseDTO> getAll() {
        return countryRepository.findAll()
                .stream()
                .map(countryMapper::toDto)
                .toList();
    }
}
