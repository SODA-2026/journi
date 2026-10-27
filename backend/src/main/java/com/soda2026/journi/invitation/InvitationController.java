package com.soda2026.journi.invitation;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvitationController {
    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }
}
