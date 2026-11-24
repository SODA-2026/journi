 package com.soda2026.journi.tripMember;

 import com.soda2026.journi.trip.TripModel;
 import com.soda2026.journi.trip.TripAccessService;
 import com.soda2026.journi.tripMember.dto.TripMemberResponse;
 import com.soda2026.journi.tripMember.dto.TripMemberEventResponse;
 import com.soda2026.journi.user.UserModel;
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
         TripMemberModel member = getMember(tripId, userId);
         UserModel actor = entityManager.find(UserModel.class, requesterId);
         if (actor == null) {
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found");
         }
         entityManager.persist(new TripMemberEventModel(
                 tripId,
                 userId,
                 member.getUser().getName(),
                 TripMemberEventType.MEMBER_REMOVED,
                 requesterId,
                 actor.getName()
         ));
         repository.delete(member);
     }

     @Transactional
     public void leaveTrip(@NotNull @Positive Long tripId, Long requesterId) {
         tripAccessService.requireTripOwnerOrMember(tripId, requesterId);
         TripModel trip = getTrip(tripId);
         requireNonOwnerMember(trip, requesterId);
         TripMemberModel member = getMember(tripId, requesterId);
         entityManager.persist(new TripMemberEventModel(
                 tripId,
                 requesterId,
                 member.getUser().getName(),
                 TripMemberEventType.MEMBER_LEFT,
                 requesterId,
                 member.getUser().getName()
         ));
         repository.delete(member);
     }

     @Transactional(readOnly = true)
     public List<TripMemberEventResponse> getEvents(@NotNull @Positive Long tripId, Long requesterId) {
         tripAccessService.requireTripOwnerOrMember(tripId, requesterId);
         return entityManager.createQuery("""
                 select event from TripMemberEventModel event where event.tripId = :tripId
                 order by event.createdAt desc, event.id desc
                 """, TripMemberEventModel.class)
                 .setParameter("tripId", tripId)
                 .setMaxResults(50)
                 .getResultList().stream()
                 .map(TripMemberEventResponse::from)
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
