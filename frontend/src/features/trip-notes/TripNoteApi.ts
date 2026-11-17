import { api } from "../../api/ApiClient";
import type { CreateTripNoteRequest, TripNote } from "./tripNotes";

export function getTripNotes(tripId: number): Promise<TripNote[]> {
    return api(`/trip-notes/${tripId}`);
}

export function createTripNote(
    data: CreateTripNoteRequest
): Promise<TripNote> {
    return api("/trip-notes", {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export function deleteTripNote(noteId: number): Promise<void> {
    return api(`/trip-notes/${noteId}`, {
        method: "DELETE",
    });
}