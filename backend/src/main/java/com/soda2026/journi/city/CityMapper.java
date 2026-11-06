package com.soda2026.journi.city;

import org.springframework.stereotype.Component;

@Component
public class CityMapper {

    public CityResponseDTO toDto(CityModel cityModel){
        return new CityResponseDTO(
                cityModel.getId(),
                cityModel.getName(),
                cityModel.getCountry().getId()
        );

    }
}
