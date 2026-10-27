package com.soda2026.journi.activityVote;

import com.soda2026.journi.activity.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityVoteService {
    private final ActivityVoteRepository voteRepository;
    private final ActivityRepository activityRepository;
    private final ActivityVoteMapper mapper;

    public ActivityVoteService(ActivityVoteRepository activityVoteRepository, ActivityRepository activityRepository, ActivityVoteMapper mapper) {
        voteRepository = activityVoteRepository;
        this.activityRepository = activityRepository;
        this.mapper = mapper;
    }

    public List<ActivityVoteResponseDTO> findByActivity(Long activityId) {
        return mapper.toDtos(voteRepository.findByIdActivityId(activityId));
    }

    public long count(Long activityId) {
        return voteRepository.countByIdActivityId(activityId);
    }



}
