package com.soda2026.journi.suggestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SuggestionModelRepository extends JpaRepository<SuggestionModel, Long> {
    @Query("""
            SELECT s FROM SuggestionModel s
            WHERE (:type IS NULL OR s.type = :type)
              AND (:destinationKind IS NULL OR s.destinationKind = :destinationKind)
              AND (:parentId IS NULL OR s.parentSuggestion.id = :parentId)
            """)
    List<SuggestionModel> search(
            @Param("type") Enum type,
            @Param("destinationKind") Enum destinationKind,
            @Param("parentId") Long parentId
    );

    // Länder och städer: destination_kind är alltid satt för destinationer och aldrig för aktiviteter.
    List<SuggestionModel> findAllByDestinationKindOrderByNameAscIdAsc(DestinationKind destinationKind);

    List<SuggestionModel> findAllByDestinationKindAndParentSuggestion_IdOrderByNameAsc(
            DestinationKind destinationKind,
            Long parentSuggestionId
    );

    Optional<SuggestionModel> findByIdAndDestinationKind(Long id, DestinationKind destinationKind);
}