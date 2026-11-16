package com.soda2026.journi.invitation;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<InvitationModel, Long> {

    Optional<InvitationModel> findByTrip_IdAndInvitee_Id(Long tripId, Long inviteeId);

    List<InvitationModel> findAllByInvitee_IdAndStatusOrderByCreatedAtDesc(Long inviteeId, InvitationStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InvitationModel i where i.id = :id")
    Optional<InvitationModel> findForUpdate(@Param("id") Long id);
}
