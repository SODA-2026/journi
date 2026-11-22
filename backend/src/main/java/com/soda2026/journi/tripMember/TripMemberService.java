 package com.soda2026.journi.tripMember;

 import com.soda2026.journi.trip.TripModel;
 import com.soda2026.journi.trip.TripAccessService;
 import com.soda2026.journi.tripMember.dto.TripMemberResponse;
 import com.soda2026.journi.tripMember.dto.TripDepartureResponse;
 import jakarta.persistence.EntityManager;
 import jakarta.validation.constraints.NotNull;
 import jakarta.validation.constraints.Positive;
 import org.springframework.http.HttpStatus;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.validation.annotation.Validated;
 import org.springframework.web.server.ResponseStatusException;

 import java.util.List;
 import java.util.Objects;

 @Service
 @Validated
 public class TripMemberService {

     private final TripMemberRepository repository;
     private final EntityManager entityManager;
     private final TripAccessService tripAccessService;

     public TripMemberService(TripMemberRepository repository, EntityManager entityManager, TripAccessService tripAccessService) {
         this.repository = repository;
         this.entityManager = entityManager;
         this.tripAccessService = tripAccessService;
     }

     @Transactional(readOnly = true)
     public List<TripMemberResponse> getMembers(
             @NotNull @Positive Long tripId,
             Long requesterId
     ) {
         tripAccessService.requireTripOwnerOrMember(tripId, requesterId);

         return repository.findAllById_TripIdOrderById_UserIdAsc(tripId)
                 .stream()
                 .map(TripMemberResponse::from)
                 .toList();
     }

     @Transactional
     public void removeMember(
             @NotNull @Positive Long tripId,
             @NotNull @Positive Long userId,
             Long requesterId
     ) {
         tripAccessService.requireTripOwner(tripId, requesterId);
         TripModel trip = getTrip(tripId);
         requireNonOwnerMember(trip, userId);
         repository.delete(getMember(tripId, userId));
     }

     @Transactional
     public void leaveTrip(@NotNull @Positive Long tripId, Long requesterId) {
         tripAccessService.requireTripOwnerOrMember(tripId, requesterId);
         TripModel trip = getTrip(tripId);
         requireNonOwnerMember(trip, requesterId);
         TripMemberModel member = getMember(tripId, requesterId);
         entityManager.persist(new TripDepartureModel(tripId, member.getUser().getName()));
         repository.delete(member);
     }

     @Transactional(readOnly = true)
     public List<TripDepartureResponse> getDepartures(@NotNull @Positive Long tripId, Long requesterId) {
         tripAccessService.requireTripOwnerOrMember(tripId, requesterId);
         return entityManager.createQuery("""
                 select d from TripDepartureModel d where d.tripId = :tripId
                 order by d.createdAt desc, d.id desc
                 """, TripDepartureModel.class)
                 .setParameter("tripId", tripId)
                 .setMaxResults(50)
                 .getResultList().stream()
                 .map(TripDepartureResponse::from)
                 .toList();
     }

     private TripModel getTrip(Long tripId) {
         TripModel trip = entityManager.find(TripModel.class, tripId);
         if (trip == null) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
         }
         return trip;
     }

     private TripMemberModel getMember(Long tripId, Long userId) {
         return repository.findById(new TripMemberId(tripId, userId))
                 .orElseThrow(() -> new ResponseStatusException(
                         HttpStatus.NOT_FOUND, "Trip member not found"
                 ));
     }

     private void requireNonOwnerMember(TripModel trip, Long userId) {
         if (Objects.equals(trip.getOwnerId(), userId)) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The trip owner cannot be removed");
         }
     }
 }

 /*
  * Owners and members may read the member list; only the owner may remove members.
  * Ownership is determined by trips.owner_id. The owner cannot be removed.
  * There is no role-change endpoint: transferring ownership is not supported.
  * New memberships are created by the invitation service after acceptance.
  * Participants may leave by deleting only their own membership; owners cannot leave.
  */
