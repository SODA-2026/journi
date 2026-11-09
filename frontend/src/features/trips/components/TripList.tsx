import { useEffect, useState } from "react";
import type { TripSummary } from "../trips";
import { getTripsByOwner } from "../TripFetch";
import { Link } from "react-router";
//TODO: ersätt med inloggad användare när spring security är på plats
const CURRENT_USER_ID = 1;

function formatDate(isoString: string): string {
    return new Date(isoString).toLocaleDateString("sv-SE", {
        day: "numeric",
        month: "short",
        year: "numeric",
    });
}

export function TripList() {
    const [trips, setTrips] = useState<TripSummary[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        getTripsByOwner(CURRENT_USER_ID)
            .then((data) => setTrips(data))
            .catch((err) => {
                console.error(err);
                setError("Kunde inte hämta dina resor");
            })
            .finally(() => setIsLoading(false));
    }, []);

    if (isLoading) {
        return <p className="text-lg text-[#1d4237]/70">Laddar resor...</p>;
    }

    if (error) {
        return (
            <div className="rounded-xl border border-red-200 bg-red-50 px-6 py-5">
                <p className="text-red-700">{error}</p>
            </div>
        );
    }

    if (trips.length === 0) {
        return (
            <div className="flex flex-col items-center gap-4 rounded-xl border border-dashed border-[#9fb0aa] px-6 py-10 text-center">
                <p className="text-lg text-[#1d4237]">
                    Du har inga sparade resor än
                </p>

                <Link
                    to="/trips/new"
                    className="rounded-lg bg-[#235e4f] px-6 py-3 text-lg text-white hover:bg-[#1d4237]"
                >
                    Skapa din första resa
                </Link>
            </div>
        );
    }

    return (
        <div className="flex flex-col gap-4">
            <h2 className="text-2xl font-bold text-[#1d4237]">Mina resor</h2>
            <ul className="flex flex-col gap-3">
                {trips.map((t) => (
                    <li
                        key={t.tripId}
                        className="rounded-xl border border-[#9fb0aa] bg-white px-6 py-5 transition hover:border-[#235e4f] hover:shadow-sm"
                    >
                        <p className="text-lg font-semibold text-[#1d4237]">
                            {t.title}
                        </p>
                        <p className="text-sm text-[#1d4237]/70">
                            Ändrad {formatDate(t.updatedAt)} · Skapad{" "}
                            {formatDate(t.createdAt)}
                        </p>
                    </li>
                ))}
            </ul>
        </div>
    );
}
