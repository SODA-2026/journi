package com.soda2026.journi.activityVote;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityVoteRepository extends JpaRepository<ActivityVoteModel, ActivityVoteId> {

    List<ActivityVoteModel> findByIdActivityId(Long activityId);
    long countByIdActivityId(Long activityId);
}
