package com.soda2026.journi.suggestion;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class SuggestionService {
    private final SuggestionModelRepository suggestionModelRepository;

    public SuggestionService(SuggestionModelRepository suggestionModelRepository) {
        this.suggestionModelRepository = suggestionModelRepository;
    }

    @Transactional(readOnly = true)
    public List<SuggestionDTO> findAll(String type, String destinationKind, Long parentId) {
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

        Instant now = Instant.now();
        suggestion.setCreatedAt(now);
        suggestion.setUpdatedAt(now);
        return toDTO(suggestionModelRepository.save(suggestion));
    }

    @Transactional
    public SuggestionDTO update(Long id, SuggestionDTO dto) {
        SuggestionModel suggestion = getSuggestion(id);
        apply(dto, suggestion);
        suggestion.setUpdatedAt(Instant.now());
        return toDTO(suggestionModelRepository.save(suggestion));
    }

    @Transactional
    public void delete(Long id) {
        suggestionModelRepository.delete(getSuggestion(id));
    }

    private SuggestionModel getSuggestion(Long id) {
        return suggestionModelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Suggestion not found"));
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
