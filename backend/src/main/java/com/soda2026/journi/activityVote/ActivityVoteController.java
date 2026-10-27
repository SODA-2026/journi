package com.soda2026.journi.activityVote;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/activities/{activityId}/votes")
public class ActivityVoteController {
    private final ActivityVoteService service;

    public ActivityVoteController(ActivityVoteService service) {
        this.service = service;
    }

    @GetMapping
    public List<ActivityVoteResponseDTO> getVotes(@PathVariable Long activityId) {
        return service.findByActivity(activityId);
    }

    @GetMapping("/count")
    public long getCount(@PathVariable Long activityId) {
        return service.count(activityId);
    }
}
