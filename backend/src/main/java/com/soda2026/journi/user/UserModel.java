package com.soda2026.journi.user;

import com.soda2026.journi.invitation.InvitationModel;
import com.soda2026.journi.note.NoteModel;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    @OneToMany(mappedBy = "author")
    private List<NoteModel> notes = new ArrayList<>();

    @OneToMany(mappedBy = "inviter")
    private List<InvitationModel> sentInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "invitee")
    private List<InvitationModel> recivedInvitations = new ArrayList<>();
}
