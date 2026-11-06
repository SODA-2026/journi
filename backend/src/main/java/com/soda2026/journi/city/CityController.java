package com.soda2026.journi.city;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CityController {
    private final CityService cityService;


    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/{countryId}")
    public ResponseEntity<List<CityResponseDTO>> getAll(@PathVariable Long countryId) {
        return ResponseEntity.ok(cityService.getAllByCountry(countryId));
    }
}