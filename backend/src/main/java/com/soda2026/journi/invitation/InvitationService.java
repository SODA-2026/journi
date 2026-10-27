package com.soda2026.journi.invitation;

import org.springframework.stereotype.Service;

@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;

    public InvitationService(InvitationRepository invitationRepository, InvitationMapper invitationMapper) {
        this.invitationRepository = invitationRepository;
        this.invitationMapper = invitationMapper;
    }
}
