package com.soda2026.journi.place;

import org.springframework.stereotype.Component;

@Component
public class PlaceMapper {

    public PlaceResponseDTO toDto(PlaceModel placeModel){
        return new PlaceResponseDTO(
                placeModel.getId(),
                placeModel.getName(),
                placeModel.getShortDescription(),
                //notera att man hämtar stadens id för i model är det en joinColoumn
                placeModel.getCity().getId()
        );
    }
}
