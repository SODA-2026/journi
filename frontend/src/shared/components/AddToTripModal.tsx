import { getMyTrips } from "../../features/trips/TripFetch";
import { useEffect, useState } from "react";
import { Link } from "react-router";
import type { TripSummary } from "../../features/trips/trips";

type AddToTripModalProps = {
    isOpen: boolean;
    itemId: number,
    itemName: string;
    itemType: "city" | "place";
    onClose: () => void;
    onAdd: (
        tripId: number,
        itemId: number
    ) => Promise<void>;
};

export function AddToTripModal({
    isOpen,
    itemId,
    itemName,
    itemType,
    onClose,
    onAdd,
}: AddToTripModalProps) {
    const [trips, setTrips] = useState<TripSummary[]>([]);
    const [selectedTripId, setSelectedTripId] = useState<number>();

    useEffect(() => {
        if (!isOpen) return;

        getMyTrips()
            .then(setTrips)
            .catch(console.error);
    }, [isOpen]);

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black/50">
            <div className="w-full max-w-md rounded-xl bg-white p-6">
                <h2 className="mb-4 text-xl font-bold">
                    Lägg till {itemType === "city" ? "stad" : "plats"} i resa
                </h2>

                {trips.length === 0 ? (
                    <div className="space-y-4">
                        <p>Du har inga resor ännu.</p>

                        <Link
                            to="/trips/new"
                            className="inline-block rounded bg-[#235e4f] px-4 py-2 text-white"
                        >
                            Skapa resa
                        </Link>
                    </div>
                ) : (
                    <div>
                        <label className="mb-2 block font-medium">
                            Välj resa:
                        </label>

                        <select 
                        value={selectedTripId ?? ""}
                        onChange={(e) => 
                            setSelectedTripId(Number(e.target.value))
                        } 
                        className="w-full rounded border p-2">
                            <option value="">
                                Välj en resa
                            </option>
                            {trips.map((trip) => (
                                <option
                                    key={trip.tripId}
                                    value={trip.tripId}
                                >
                                    {trip.title}
                                </option>
                            ))}
                        </select>
                        <button
                            type="button"
                            disabled={!selectedTripId}
                            className="mt-4 w-full rounded-lg bg-[#235e4f] px-4 py-2 text-white disabled:opacity-50"
                            onClick={async () => {
                                if (!selectedTripId) return;

                                await onAdd(
                                    selectedTripId,
                                    itemId
                                );

                                onClose();
                            }}
                        >
                            Lägg till i plan
                        </button>
                    </div>
                )}

                <button
                    onClick={onClose}
                    className="mb-4 rounded border px-3 py-2"
                >
                    Stäng
                </button>

                {/* Innehållet kommer här */}
            </div>
        </div>
    );
}