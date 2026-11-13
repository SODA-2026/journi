 package com.soda2026.journi.tripNote;

 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.stereotype.Repository;

 @Repository
 public interface TripNoteRepository extends JpaRepository<TripNoteModel, Long> {
 }
