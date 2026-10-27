package com.soda2026.journi.note.Dto;


import java.time.LocalDateTime;

public class NoteResponseDto {
    private Long id;
    private Long tripId;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NoteResponseDto() {
    }

    public NoteResponseDto(Long id, Long tripId, Long authorId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tripId = tripId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTripId() {
        return tripId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
