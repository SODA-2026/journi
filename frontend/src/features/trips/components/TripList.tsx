import { useEffect, useState } from "react";
import type { TripSummary } from "../trips";
import { getMyTrips } from "../TripFetch";
import { Link } from "react-router";
import { formatDate } from "../../../shared/formatDate";
import { DeleteTripButton } from "./DeleteTripButton";



export function TripList() {
    const [trips, setTrips] = useState<TripSummary[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        getMyTrips()
            .then((data) => setTrips(data))
            .catch((err) => {
                console.error(err);
                setError("Kunde inte hämta dina resor");
            })
            .finally(() => setIsLoading(false));
    }, []);

    function handleTripDeleted(tripId: number) {
        setTrips((current) => current.filter((trip) => trip.tripId !== tripId));
    }

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
                        className="relative flex items-center justify-between gap-4 rounded-xl border border-[#9fb0aa] bg-white px-6 py-5 transition hover:border-[#235e4f]"
                     >
                            <div>
                              <Link
                                to={/trips/${t.tripId}}
                                className="text-lg font-semibold text-[#1d4237] after:absolute after:inset-0"
                              >        
                                {t.title}
                              </Link>
                              <p className="text-sm text-[#1d4237]/70">
                                Avresa {formatDate(t.startDate)} · Skapad {formatDate(t.createdAt)}
                              </p>
                            </div>

                            <div className="relative z-10">
                              <DeleteTripButton
                                tripId={t.tripId}
                                title={t.title}
                                onDeleted={handleTripDeleted}
                              />
                            </div>      
                    </li>
                ))}
            </ul>
        </div>
    );
}
