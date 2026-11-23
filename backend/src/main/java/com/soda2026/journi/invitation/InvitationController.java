package com.soda2026.journi.invitation;

import com.soda2026.journi.invitation.Dto.InvitationRequestDto;
import com.soda2026.journi.invitation.Dto.InvitationResponseDto;
import com.soda2026.journi.invitation.Dto.SentInvitationDto;
import com.soda2026.journi.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationResponseDto sendInvitation(
            @Valid @RequestBody InvitationRequestDto invitationRequest,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return invitationService.sendInvitation(invitationRequest, user.getId());
    }

    @GetMapping("/mine")
    public List<InvitationResponseDto> getIncomingInvitations(@AuthenticationPrincipal UserPrincipal user) {
        return invitationService.getIncomingInvitations(user.getId());
    }

    @GetMapping("/sent")
    public List<SentInvitationDto> getSentInvitations(@AuthenticationPrincipal UserPrincipal user) {
        return invitationService.getSentInvitations(user.getId());
    }

    @PostMapping("/{id}/accept")
    public InvitationResponseDto accept(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return invitationService.respondToInvitation(id, user.getId(), true);
    }

    @PostMapping("/{id}/decline")
    public InvitationResponseDto decline(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return invitationService.respondToInvitation(id, user.getId(), false);
    }
}
