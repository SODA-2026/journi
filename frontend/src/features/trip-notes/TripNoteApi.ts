import { api } from "../../api/ApiClient";
import type { CreateTripNoteRequest, TripNote } from "./tripNotes";

export function getTripNotes(tripId: number): Promise<TripNote[]> {
    return api(`/trips/${tripId}/notes`);
}

export function createTripNote(
    tripId: number,
    data: CreateTripNoteRequest
): Promise<TripNote> {
    return api(`/trips/${tripId}/notes`, {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export function deleteTripNote(tripId: number, noteId: number): Promise<void> {
    return api(`/trips/${tripId}/notes/${noteId}`, {
        method: "DELETE",
    });
}