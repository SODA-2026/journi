import { useEffect, useState } from "react";
import type { TripSummary } from "../trips";
import { getMyTrips } from "../TripFetch";
import { Link } from "react-router";
import { formatDate } from "../../../shared/formatDate";
import { DeleteTripButton } from "./DeleteTripButton";
import { useAuth } from "../../../context/AuthContext";

export function TripList() {
    const { user } = useAuth();
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

    const ownedTrips = trips.filter((trip) => trip.ownerId === user?.id);
    const invitedTrips = trips.filter((trip) => trip.ownerId !== user?.id);

    function renderTrips(items: TripSummary[], isOwner: boolean) {
        return (
            <ul className="flex flex-col gap-3">
                {items.map((trip) => (
                    <li
                        key={trip.tripId}
                        className="relative flex items-center justify-between gap-4 rounded-xl border border-[#9fb0aa] bg-white px-6 py-5 transition hover:border-[#235e4f]"
                    >
                        <div>
                            <Link
                                to={`/trips/${trip.tripId}`}
                                className="text-lg font-semibold text-[#1d4237] after:absolute after:inset-0"
                            >
                                {trip.title}
                            </Link>
                            <p className="mt-1 text-sm font-medium text-[#235e4f]">
                                {isOwner
                                    ? "Du är skapare"
                                    : "Du är inbjuden deltagare"}
                            </p>
                            <p className="text-sm text-[#1d4237]/70">
                                Skapare: {trip.ownerName}
                            </p>
                            <p className="text-sm text-[#1d4237]/70">
                                Avresa {formatDate(trip.startDate)} · Skapad {formatDate(trip.createdAt)}
                            </p>
                        </div>

                        {isOwner && (
                            <div className="relative z-10">
                                <DeleteTripButton
                                    tripId={trip.tripId}
                                    title={trip.title}
                                    onDeleted={handleTripDeleted}
                                />
                            </div>
                        )}
                    </li>
                ))}
            </ul>
        );
    }

    return (
        <div className="flex flex-col gap-4">
            <h2 className="text-2xl font-bold text-[#1d4237]">Mina resor</h2>
            {ownedTrips.length > 0 && renderTrips(ownedTrips, true)}
            {invitedTrips.length > 0 && (
                <section aria-labelledby="invited-trips-heading">
                    <h3 id="invited-trips-heading" className="mb-3 text-xl font-bold text-[#1d4237]">
                        Resor du deltar i
                    </h3>
                    {renderTrips(invitedTrips, false)}
                </section>
            )}
        </div>
    );
}
