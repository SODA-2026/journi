package com.soda2026.journi.suggestion;

import com.soda2026.journi.suggestion.dto.CityDTO;
import com.soda2026.journi.suggestion.dto.CountryDTO;
import com.soda2026.journi.suggestion.dto.SuggestionDTO;
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
    private final SuggestionMapper suggestionMapper;

    public SuggestionService(SuggestionModelRepository suggestionModelRepository, SuggestionMapper suggestionMapper) {
        this.suggestionModelRepository = suggestionModelRepository;
        this.suggestionMapper = suggestionMapper;
    }

    @Transactional(readOnly = true)
    public List<SuggestionDTO> findAll(SuggestionType type, DestinationKind destinationKind, Long parentId) {
        return suggestionMapper.toDtos(suggestionModelRepository.search(type, destinationKind, parentId));
    }

    @Transactional(readOnly = true)
    public SuggestionDTO findById(Long id) {
        return suggestionMapper.toDto(getSuggestion(id));
    }

    @Transactional
    public SuggestionDTO create(SuggestionDTO dto) {
        SuggestionModel suggestion = new SuggestionModel();
        apply(dto, suggestion);

        LocalDateTime now = LocalDateTime.now();
        suggestion.setCreatedAt(now);
        suggestion.setUpdatedAt(now);
        return suggestionMapper.toDto(suggestionModelRepository.save(suggestion));
    }

    @Transactional
    public SuggestionDTO update(Long id, SuggestionDTO dto) {
        SuggestionModel suggestion = getSuggestion(id);
        apply(dto, suggestion);
        suggestion.setUpdatedAt(LocalDateTime.now());
        return suggestionMapper.toDto(suggestionModelRepository.save(suggestion));
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
            uniqueByName.putIfAbsent(country.getName().strip().toLowerCase(Locale.ROOT), suggestionMapper.toCountryDto(country));
        }
        return List.copyOf(uniqueByName.values());
    }

    @Transactional(readOnly = true)
    public List<CityDTO> findCitiesByCountry(Long countryId) {
        getCountry(countryId);
        return suggestionModelRepository
                .findAllByDestinationKindAndParentSuggestion_IdOrderByNameAsc(DestinationKind.CITY, countryId)
                .stream()
                .map(city -> suggestionMapper.toCityDto(city, countryId))
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

    private void apply(SuggestionDTO dto, SuggestionModel suggestion) {
        if (dto.type() == null || dto.name() == null || dto.name().isBlank()
                || dto.shortDescription() == null || dto.shortDescription().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Type, name, and short description are required"
            );
        }

        boolean isDestination = dto.type() == SuggestionType.DESTINATION;
        boolean isActivity = dto.type() == SuggestionType.ACTIVITY;
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

        SuggestionModel parent = null;
        if (dto.parentSuggestionId() != null) {
            parent = getSuggestion(dto.parentSuggestionId());
            if (parent.getType() != SuggestionType.DESTINATION) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A parent suggestion must be a destination"
                );
            }
        }

        suggestionMapper.updateEntity(dto, parent, suggestion);
    }
}
