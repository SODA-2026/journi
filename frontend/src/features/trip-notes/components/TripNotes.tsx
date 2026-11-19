import { useEffect, useState } from "react";
import { deleteTripNote, getTripNotes } from "../TripNoteApi";
import type { TripNote } from "../tripNotes";
import { TripNoteForm } from "./TripNoteForm";
import { TripNotesList } from "./TripNotesList";
import { useAuth } from "../../../context/AuthContext";

type TripNotesProps = {
    tripId: number;
    tripOwnerId: number;
};

export function TripNotes({ tripId, tripOwnerId }: TripNotesProps) {
    const { user } = useAuth();
    const [notes, setNotes] = useState<TripNote[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [deleteError, setDeleteError] = useState("");
    const [deletingNoteId, setDeletingNoteId] = useState<number | null>(null);
    const [isFormVisible, setIsFormVisible] = useState(false);

    useEffect(() => {
        async function loadNotes() {
            setIsLoading(true);
            setError("");

            try {
                const data = await getTripNotes(tripId);
                setNotes(data);
            } catch (error) {
                console.error("Failed to load trip notes:", error);
                setError("Kunde inte hämta anteckningarna");
            } finally {
                setIsLoading(false);
            }
        }

        loadNotes();
    }, [tripId]);

    async function handleDelete(noteId: number) {
        if (!window.confirm("Vill du ta bort anteckningen?")) return;

        setDeletingNoteId(noteId);
        setDeleteError("");

        try {
            await deleteTripNote(tripId, noteId);
            setNotes((current) => current.filter((note) => note.id !== noteId));
        } catch (error) {
            console.error("Failed to delete trip note:", error);
            setDeleteError("Kunde inte ta bort anteckningen");
        } finally {
            setDeletingNoteId(null);
        }
    }

    if (isLoading) {
        return <p>Laddar anteckningar...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    return (
        <section>
            <div>
                <h3>Anteckningar</h3>
                <button
                    type="button"
                    onClick={() => setIsFormVisible((visible) => !visible)}
                >
                    {isFormVisible ? "Avbryt" : "Lägg till anteckning"}
                </button>
            </div>

            {isFormVisible && (
                <TripNoteForm
                    tripId={tripId}
                    onCreated={(note) => {
                        setNotes((current) => [note, ...current]);
                        setIsFormVisible(false);
                    }}
                />
            )}

            <TripNotesList
                notes={notes}
                deletingNoteId={deletingNoteId}
                onDelete={handleDelete}
                currentUserId={user?.id ?? null}
                tripOwnerId={tripOwnerId}
            />
            {deleteError && <p role="alert">{deleteError}</p>}
        </section>
    );
}