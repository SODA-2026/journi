package com.soda2026.journi.activityVote;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityVoteMapper {

    public ActivityVoteResponseDTO toDto(ActivityVoteModel model) {
        return new ActivityVoteResponseDTO(
                model.getId().getActivityId(),
                model.getId().getUserId(),
                model.getCreatedAt());
    }

    public List<ActivityVoteResponseDTO> toDtos(List<ActivityVoteModel> models) {
        return models.stream().map(this::toDto).toList();
    }
}
