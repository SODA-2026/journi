 package com.soda2026.journi.tripMember;

 import com.soda2026.journi.trip.TripModel;
 import com.soda2026.journi.trip.TripAccessService;
 import com.soda2026.journi.user.UserModel;
 import com.soda2026.journi.tripMember.dto.TripMemberResponse;
 import jakarta.persistence.EntityManager;
 import jakarta.validation.constraints.NotNull;
 import jakarta.validation.constraints.Positive;
 import org.springframework.http.HttpStatus;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.validation.annotation.Validated;
 import org.springframework.web.server.ResponseStatusException;

 import java.security.Principal;
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
             Principal principal
     ) {
         UserModel requester = getCurrentUser(principal);
         tripAccessService.requireTripOwnerOrMember(tripId, requester.getId());

         return repository.findAllById_TripIdOrderById_UserIdAsc(tripId)
                 .stream()
                 .map(TripMemberResponse::from)
                 .toList();
     }

     @Transactional
     public void removeMember(
             @NotNull @Positive Long tripId,
             @NotNull @Positive Long userId,
             Principal principal
     ) {
         UserModel requester = getCurrentUser(principal);
         tripAccessService.requireTripOwner(tripId, requester.getId());
         TripModel trip = getTrip(tripId);
         requireNonOwnerMember(trip, userId);
         repository.delete(getMember(tripId, userId));
     }

     private UserModel getCurrentUser(Principal principal) {
         if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
         }

         return entityManager.createQuery(
                         "select u from UserModel u where u.email = :email", UserModel.class
                 )
                 .setParameter("email", principal.getName())
                 .getResultStream()
                 .findFirst()
                 .orElseThrow(() -> new ResponseStatusException(
                         HttpStatus.UNAUTHORIZED, "Authenticated user not found"
                 ));
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
  * Principal.getName() supplies the authenticated user's email.
  * New memberships are created by the invitation service after acceptance.
  */
