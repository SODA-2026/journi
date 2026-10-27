package com.soda2026.journi.trip;

import com.soda2026.journi.invitation.InvitationModel;
import com.soda2026.journi.note.NoteModel;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

public class TripModel {
    @OneToMany(mappedBy = "trip")
    private List<NoteModel> notes = new ArrayList<>();

    @OneToMany(mappedBy = "trip")
    private List<InvitationModel> invitations = new ArrayList<>();
}
