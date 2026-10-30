package com.soda2026.journi.suggestion;

import com.soda2026.journi.suggestion.dto.CityDTO;
import com.soda2026.journi.suggestion.dto.CountryDTO;
import com.soda2026.journi.suggestion.dto.SuggestionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/suggestions")
public class SuggestionController {
    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping
    public ResponseEntity<List<SuggestionDTO>> findAll(
            @RequestParam(required = false) SuggestionType type,
            @RequestParam(required = false) DestinationKind destinationKind,
            @RequestParam(required = false) Long parentId
    ) {
        return ResponseEntity.ok(suggestionService.findAll(type, destinationKind, parentId));
    }

    @GetMapping("/countries")
    public ResponseEntity<List<CountryDTO>> findCountries() {
        return ResponseEntity.ok(suggestionService.findCountries());
    }

    @GetMapping("/countries/{countryId}/cities")
    public ResponseEntity<List<CityDTO>> findCitiesByCountry(@PathVariable("countryId") Long countryId) {
        return ResponseEntity.ok(suggestionService.findCitiesByCountry(countryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuggestionDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(suggestionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SuggestionDTO> create(@RequestBody SuggestionDTO dto) {
        SuggestionDTO created = suggestionService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuggestionDTO> update(@PathVariable Long id, @RequestBody SuggestionDTO dto) {
        return ResponseEntity.ok(suggestionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        suggestionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
