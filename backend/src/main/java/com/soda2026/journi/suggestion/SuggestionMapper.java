package com.soda2026.journi.suggestion;

import com.soda2026.journi.suggestion.dto.CityDTO;
import com.soda2026.journi.suggestion.dto.CountryDTO;
import com.soda2026.journi.suggestion.dto.SuggestionDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SuggestionMapper {

    public SuggestionDTO toDto(SuggestionModel suggestion) {
        return new SuggestionDTO(
                suggestion.getId(),
                suggestion.getType(),
                suggestion.getDestinationKind(),
                suggestion.getParentSuggestion() == null ? null : suggestion.getParentSuggestion().getId(),
                suggestion.getName(),
                suggestion.getCountry(),
                suggestion.getRegion(),
                suggestion.getCategory(),
                suggestion.getShortDescription(),
                suggestion.getDescription(),
                suggestion.getBestTravelTime(),
                suggestion.getImageUrl(),
                suggestion.getCreatedAt(),
                suggestion.getUpdatedAt()
        );
    }

    public List<SuggestionDTO> toDtos(List<SuggestionModel> suggestions) {
        return suggestions.stream().map(this::toDto).toList();
    }

    public CountryDTO toCountryDto(SuggestionModel country) {
        return new CountryDTO(country.getId(), country.getName());
    }

    public CityDTO toCityDto(SuggestionModel city, Long countryId) {
        return new CityDTO(city.getId(), countryId, city.getName());
    }

    public void updateEntity(SuggestionDTO dto, SuggestionModel parent, SuggestionModel suggestion) {
        suggestion.setType(dto.type());
        suggestion.setDestinationKind(dto.destinationKind());
        suggestion.setParentSuggestion(parent);
        suggestion.setName(dto.name());
        suggestion.setCountry(dto.country());
        suggestion.setRegion(dto.region());
        suggestion.setCategory(dto.category());
        suggestion.setShortDescription(dto.shortDescription());
        suggestion.setDescription(dto.description());
        suggestion.setBestTravelTime(dto.bestTravelTime());
        suggestion.setImageUrl(dto.imageUrl());
    }
}
