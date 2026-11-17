 package com.soda2026.journi.tripNote;

 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.stereotype.Repository;

 import java.util.List;

 @Repository
 public interface TripNoteRepository extends JpaRepository<TripNoteModel, Long> {
  List<TripNoteModel> findAllByTrip_IdOrderByUpdatedAtDesc(Long tripId);
 }
