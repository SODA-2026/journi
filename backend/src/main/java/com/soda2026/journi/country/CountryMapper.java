package com.soda2026.journi.country;

import org.springframework.stereotype.Component;

@Component
public class CountryMapper {

    public CountryResponseDTO toDto(CountryModel countryModel){
        return new CountryResponseDTO(
                countryModel.getId(),
                countryModel.getName()
        );
    }
}
