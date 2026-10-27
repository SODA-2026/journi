package com.soda2026.journi.suggestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SuggestionModelRepository extends JpaRepository<SuggestionModel, Long> {
    @Query("""
            SELECT s FROM SuggestionModel s
            WHERE (:type IS NULL OR s.type = :type)
              AND (:destinationKind IS NULL OR s.destinationKind = :destinationKind)
              AND (:parentId IS NULL OR s.parentSuggestion.id = :parentId)
            """)
    List<SuggestionModel> search(
            @Param("type") String type,
            @Param("destinationKind") String destinationKind,
            @Param("parentId") Long parentId
    );
}