package com.soda2026.journi.activity;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityMapper {

    public ActivityResponseDTO toDto(ActivityModel model) {
        return new ActivityResponseDTO(
                model.getId(),
                model.getTripDestinationId(),
                model.getName(),
                model.getActivityDate(),
                model.getStartTime(),
                model.getDescription(),
                model.getStatus(),
                model.getCreatedAt(),
                model.getUpdatedAt());
    }

    public List<ActivityResponseDTO> toDtos(List<ActivityModel> models) {
        return models.stream().map(this::toDto).toList();
    }

    public ActivityModel toEntity(ActivityResponseDTO dto) {
        ActivityModel model = new ActivityModel();
        updateEntity(dto, model);
        return model;
    }

    public void updateEntity(ActivityResponseDTO dto, ActivityModel model) {
        model.setName(dto.name());
        model.setActivityDate(dto.activityDate());
        model.setStartTime(dto.startTime());
        model.setDescription(dto.description());
        model.setTripDestinationId(dto.tripDestinationId());
        if (dto.status() != null) {
            model.setStatus(dto.status());
        }
    }
}
