package com.soda2026.journi.place;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {

        this.placeService = placeService;
    }

    @GetMapping("/city/{cityId}")
    public List<PlaceResponseDTO> getPlaceByCity(@PathVariable Long cityId) {
        return placeService.getPlacesByCity(cityId);
    }
}
