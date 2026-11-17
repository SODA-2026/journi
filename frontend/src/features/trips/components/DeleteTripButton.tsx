import { useState } from "react";
import { deleteTrip } from "../TripFetch";

type DeleteTripButtonProps = {
    tripId: number;
    title: string;
    onDeleted: (tripId: number) => void;
};

export function DeleteTripButton({ tripId, title, onDeleted }: DeleteTripButtonProps) {
    const [deleting, setDeleting] = useState(false);
    const [error, setError] = useState("");

    async function handleDelete() {
        if (deleting) return;
        if (!window.confirm(`Delete "${title}"? This can't be undone.`)) return;

        setDeleting(true);
        setError("");
        try {
            await deleteTrip(tripId);
            onDeleted(tripId);
        } catch (err) {
            console.error("Failed to delete trip:", err);
            setError("Could not delete the trip. Please try again.");
            setDeleting(false);
        }
    }

    return (
        <div className="shrink=0">
            <button
                type="button"
                onClick={handleDelete}
                disabled={deleting}
                aria-label={`Delete trip ${title}`}
                className="rounded-lg border border-red-600 px-4 py-2 text-red-700 hover:bg-red-600 hover:text-white focus-visible:ring-2 focus-visible:ring-red-600 disabled:opacity-50"
            >
                {deleting ? "Deleting…" : "Delete"}
            </button>
            {error && <p role="alert" className="mt-2 text-sm text-red-700">{error}</p>}
        </div>
    );
}