import { formatDate } from "../../../shared/formatDate";
import type { TripNote } from "../tripNotes";

type TripNotesListProps = {
    notes: TripNote[];
    deletingNoteId: number | null;
    onDelete: (noteId: number) => void;
};

export function TripNotesList({
    notes,
    deletingNoteId,
    onDelete,
}: TripNotesListProps) {
    if (notes.length === 0) {
        return <p>Det finns inga anteckningar ännu.</p>;
    }

    return (
        <ul>
            {notes.map((note) => (
                <li key={note.id}>
                    <p>{note.content}</p>
                    <small>{formatDate(note.createdAt)}</small>
                    <button
                        type="button"
                        onClick={() => onDelete(note.id)}
                        disabled={deletingNoteId === note.id}
                    >
                        {deletingNoteId === note.id ? "Tar bort..." : "Ta bort"}
                    </button>
                </li>
            ))}
        </ul>
    );
}