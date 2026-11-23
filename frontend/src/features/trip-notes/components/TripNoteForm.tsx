import { useState, type FormEvent } from "react";
import { createTripNote } from "../TripNoteApi";
import type { TripNote } from "../tripNotes";

type TripNoteFormProps = {
    tripId: number;
    onCreated: (note: TripNote) => void;
};

export function TripNoteForm({ tripId, onCreated }: TripNoteFormProps) {
    const [content, setContent] = useState("");
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState("");

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        if (!content.trim() || isSaving) return;

        setIsSaving(true);
        setError("");

        try {
            const note = await createTripNote(tripId, {
                content: content.trim(),
            });
            setContent("");
            onCreated(note);
        } catch (error) {
            console.error("Failed to create trip note:", error);
            setError("Kunde inte spara anteckningen");
        } finally {
            setIsSaving(false);
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <label htmlFor="trip-note-content">Ny anteckning</label>
            <textarea
                id="trip-note-content"
                value={content}
                onChange={(event) => setContent(event.target.value)}
                disabled={isSaving}
                required
                rows={4}
            />
            <button type="submit" disabled={isSaving || !content.trim()}>
                {isSaving ? "Sparar..." : "Spara anteckning"}
            </button>
            {error && <p role="alert">{error}</p>}
        </form>
    );
}