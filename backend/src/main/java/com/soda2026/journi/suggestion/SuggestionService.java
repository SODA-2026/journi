package com.soda2026.journi.suggestion;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SuggestionService {
    private final SuggestionModelRepository suggestionModelRepository;

    public SuggestionService(SuggestionModelRepository suggestionModelRepository) {
        this.suggestionModelRepository = suggestionModelRepository;
    }

    @Transactional(readOnly = true)
    public List<SuggestionDTO> findAll(Enum type, Enum destinationKind, Long parentId) {
        return suggestionModelRepository.search(type, destinationKind, parentId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SuggestionDTO findById(Long id) {
        return toDTO(getSuggestion(id));
    }

    @Transactional
    public SuggestionDTO create(SuggestionDTO dto) {
        SuggestionModel suggestion = new SuggestionModel();
        apply(dto, suggestion);

        LocalDateTime now = LocalDateTime.now();
        suggestion.setCreatedAt(now);
        suggestion.setUpdatedAt(now);
        return toDTO(suggestionModelRepository.save(suggestion));
    }

    @Transactional
    public SuggestionDTO update(Long id, SuggestionDTO dto) {
        SuggestionModel suggestion = getSuggestion(id);
        apply(dto, suggestion);
        suggestion.setUpdatedAt(LocalDateTime.now());
        return toDTO(suggestionModelRepository.save(suggestion));
    }

    @Transactional
    public void delete(Long id) {
        suggestionModelRepository.delete(getSuggestion(id));
    }

    @Transactional(readOnly = true)
    public List<CountryDTO> findCountries() {
        Map<String, CountryDTO> uniqueByName = new LinkedHashMap<>();
        for (SuggestionModel country : suggestionModelRepository
                .findAllByDestinationKindOrderByNameAscIdAsc(DestinationKind.COUNTRY)) {
            uniqueByName.putIfAbsent(country.getName().strip().toLowerCase(Locale.ROOT), toCountryDTO(country));
        }
        return List.copyOf(uniqueByName.values());
    }

    @Transactional(readOnly = true)
    public List<CityDTO> findCitiesByCountry(Long countryId) {
        getCountry(countryId);
        return suggestionModelRepository
                .findAllByDestinationKindAndParentSuggestion_IdOrderByNameAsc(DestinationKind.CITY, countryId)
                .stream()
                .map(city -> toCityDTO(city, countryId))
                .toList();
    }

    /**
     * Kontrollerar att landet och staden finns och att staden hör till landet.
     * 400 vid saknade id eller fel kombination, 404 om land eller stad inte finns.
     */
    @Transactional(readOnly = true)
    public void validateCityBelongsToCountry(Long countryId, Long cityId) {
        if (countryId == null || cityId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Country and city are required");
        }

        getCountry(countryId);
        SuggestionModel city = getCity(cityId);

        SuggestionModel parent = city.getParentSuggestion();
        if (parent == null || !countryId.equals(parent.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "City does not belong to the selected country"
            );
        }
    }

    private SuggestionModel getSuggestion(Long id) {
        return suggestionModelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Suggestion not found"));
    }

    private SuggestionModel getCountry(Long countryId) {
        return suggestionModelRepository.findByIdAndDestinationKind(countryId, DestinationKind.COUNTRY)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Country not found"));
    }

    private SuggestionModel getCity(Long cityId) {
        return suggestionModelRepository.findByIdAndDestinationKind(cityId, DestinationKind.CITY)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
    }

    private CountryDTO toCountryDTO(SuggestionModel country) {
        return new CountryDTO(country.getId(), country.getName());
    }

    private CityDTO toCityDTO(SuggestionModel city, Long countryId) {
        return new CityDTO(city.getId(), countryId, city.getName());
    }

    private void apply(SuggestionDTO dto, SuggestionModel suggestion) {
        if (dto.type() == null || dto.name() == null || dto.name().isBlank()
                || dto.shortDescription() == null || dto.shortDescription().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Type, name, and short description are required"
            );
        }

        boolean isDestination = "DESTINATION".equals(dto.type());
        boolean isActivity = "ACTIVITY".equals(dto.type());
        if (!isDestination && !isActivity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Type must be DESTINATION or ACTIVITY");
        }
        if (isDestination && (dto.destinationKind() == null || dto.country() == null || dto.country().isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Destinations require a destination kind and country"
            );
        }
        if (isActivity && (dto.destinationKind() != null || dto.category() == null || dto.category().isBlank()
                || dto.parentSuggestionId() == null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Activities require a category and parent destination, and cannot have a destination kind"
            );
        }
        if (dto.destinationKind() != null && !List.of("COUNTRY", "CITY", "REGION", "PLACE")
                .contains(dto.destinationKind())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Destination kind must be COUNTRY, CITY, REGION, or PLACE"
            );
        }

        SuggestionModel parent = null;
        if (dto.parentSuggestionId() != null) {
            parent = getSuggestion(dto.parentSuggestionId());
            if (!"DESTINATION".equals(parent.getType())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A parent suggestion must be a destination"
                );
            }
        }

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

    private SuggestionDTO toDTO(SuggestionModel suggestion) {
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
}
