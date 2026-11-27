 package com.soda2026.journi.tripActivity;

 import com.soda2026.journi.place.PlaceModel;
 import com.soda2026.journi.tripActivity.dto.TripActivityRequestDTO;
 import com.soda2026.journi.tripActivity.dto.UpdateActivityRequestDTO;
 import com.soda2026.journi.tripDestination.TripDestinationModel;
 import com.soda2026.journi.user.UserModel;
 import org.springframework.stereotype.Component;
 import com.soda2026.journi.tripActivity.dto.TripActivityResponseDTO;
 import java.util.List;

 @Component
 public class TripActivityMapper {

     public TripActivityResponseDTO toDto(TripActivityModel tripActivityModel) {
         UserModel creator = tripActivityModel.getCreatedBy();

         return new TripActivityResponseDTO(
                tripActivityModel.getId(),
                tripActivityModel.getTripDestination().getId(),
                tripActivityModel.getName(),
                tripActivityModel.getActivityDate(),
                tripActivityModel.getStartTime(),
                tripActivityModel.getDescription(),
                tripActivityModel.getStatus(),
                creator != null ? creator.getId() : null, creator != null ? creator.getName() : null
        );
     }

     public List<TripActivityResponseDTO> toDtos(List<TripActivityModel> models) {
         return models.stream().map(this::toDto).toList();
     }

     public TripActivityModel toEntity(TripActivityRequestDTO dto, TripDestinationModel tripDestination, PlaceModel place) {
         TripActivityModel model = new TripActivityModel();
         updateEntity(dto, model, tripDestination, place);
         return model;
     }

     public void updateEntity(TripActivityRequestDTO dto, TripActivityModel model, TripDestinationModel tripDestination, PlaceModel place) {
         model.setName(dto.name());
         model.setActivityDate(dto.activityDate());
         model.setStartTime(dto.startTime());
         model.setDescription(dto.description());
         model.setTripDestination(tripDestination);
         model.setPlace(place);
     }

     public void updateEntity(UpdateActivityRequestDTO dto, TripActivityModel model) {
         model.setName(dto.name());
         model.setActivityDate(dto.activityDate());
         model.setStartTime(dto.startTime());
         model.setDescription(dto.description());
     }

//     public TripActivityModel toEntity(TripActivityRequestDTO tripActivityRequestDTO, TripDestinationModel tripDestinationModel) {
//         return new TripActivityModel(
//                 tripActivityRequestDTO.placeId(),
//
//                 tripActivityRequestDTO.name(),
//                 tripActivityRequestDTO.activityDate(),
//                 tripActivityRequestDTO.startTime(),
//                 tripActivityRequestDTO.description()
//         );
//     }
 }
