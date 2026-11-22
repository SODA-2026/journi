 package com.soda2026.journi.tripMember;

 import com.soda2026.journi.security.UserPrincipal;
 import com.soda2026.journi.tripMember.dto.TripMemberResponse;
 import jakarta.validation.constraints.Positive;
 import org.springframework.http.HttpStatus;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
 import org.springframework.web.bind.annotation.DeleteMapping;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.ResponseStatus;
 import org.springframework.web.bind.annotation.RestController;

 import java.util.List;

 @RestController
 @RequestMapping("/api/trips/{tripId}/members")
 public class TripMemberController {

     private final TripMemberService service;

     public TripMemberController(TripMemberService service) {
         this.service = service;
     }

     @GetMapping
     public List<TripMemberResponse> getMembers(
             @PathVariable("tripId") @Positive Long tripId,
             @AuthenticationPrincipal UserPrincipal user
     ) {
         return service.getMembers(tripId, user.getId());
     }

     @DeleteMapping("/{userId}")
     @ResponseStatus(HttpStatus.NO_CONTENT)
     public void removeMember(
             @PathVariable("tripId") @Positive Long tripId,
             @PathVariable("userId") @Positive Long userId,
             @AuthenticationPrincipal UserPrincipal user
     ) {
         service.removeMember(tripId, userId, user.getId());
     }
 }

 /*
  * GET lists members; DELETE removes a non-owner member.
  * Memberships are created through accepted invitations.
  * Role changes and ownership transfers are not exposed by this API.
  */
