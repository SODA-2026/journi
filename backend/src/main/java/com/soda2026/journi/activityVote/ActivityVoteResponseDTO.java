package com.soda2026.journi.activityVote;

import java.time.Instant;

public record ActivityVoteResponseDTO(
        Long activityId,
        Long userId,
        Instant createdAt) {}
