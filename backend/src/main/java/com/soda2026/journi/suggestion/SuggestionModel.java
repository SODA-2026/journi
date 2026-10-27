package com.soda2026.journi.suggestion;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "suggestions")
public class SuggestionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    @Lob
    @Column(name = "destination_kind")
    private String destinationKind;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "parent_suggestion_id")
    private SuggestionModel parentSuggestion;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "region", length = 150)
    private String region;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "short_description", nullable = false, length = 500)
    private String shortDescription;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "best_travel_time", length = 150)
    private String bestTravelTime;

    @Column(name = "image_url")
    private String imageUrl;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "parentSuggestion")
    private Set<SuggestionModel> suggestions = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDestinationKind() {
        return destinationKind;
    }

    public void setDestinationKind(String destinationKind) {
        this.destinationKind = destinationKind;
    }

    public SuggestionModel getParentSuggestion() {
        return parentSuggestion;
    }

    public void setParentSuggestion(SuggestionModel parentSuggestion) {
        this.parentSuggestion = parentSuggestion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBestTravelTime() {
        return bestTravelTime;
    }

    public void setBestTravelTime(String bestTravelTime) {
        this.bestTravelTime = bestTravelTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<SuggestionModel> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(Set<SuggestionModel> suggestions) {
        this.suggestions = suggestions;
    }

}