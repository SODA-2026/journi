import { useEffect, useState } from "react";
import type { TripInfoType } from "../trips";
import { getTripInfo } from "../TripFetch";
import { Link, useParams } from "react-router";
import { formatDate } from "../../../shared/formatDate";
import { ApiError } from "../../../api/ApiClient";
import { useAuth } from "../../../context/AuthContext";
import { InviteUserForm } from "../../invitations/InviteUserForm";

export function TripInfo() {
    const { user } = useAuth();
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [trip, setTrip] = useState<TripInfoType>();

    const params = useParams();
    const tripId = Number(params.id);

    useEffect(() => {
        getTripInfo(tripId)
            .then((data) => setTrip(data))
            .catch((err) => {
                console.error(err);
                if (err instanceof ApiError && err.status === 404) {
                    setError("resan hittades inte");
                } else if (err instanceof ApiError && err.status === 403) {
                    setError("Du har inte tillgång till resan");
                } else setError("Kunde inte hämta resan, försök igen");
            })
            .finally(() => setIsLoading(false));
    }, [tripId]);
    if (isLoading) return <p>Laddar resa...</p>;
    if (error)
        return (
            <div>
                <Link to={"/trips"}>Tillbaka</Link>
                <p>{error}</p>
            </div>
        );
    if (!trip) return <p>Resan finns inte</p>;

    return (
        <div>
            <Link to={"/trips"}>Tillbaka</Link>
            <h2>{trip.title}</h2>
            <p className="mt-1 font-medium text-[#235e4f]">
                {user?.id === trip.ownerId
                    ? "Du är skapare"
                    : "Du är inbjuden deltagare"}
            </p>
            <p>Skapare: {trip.ownerName}</p>
            {user?.id === trip.ownerId && (
                <InviteUserForm key={trip.id} tripId={trip.id} />
            )}
            <section>
                <p>Destinationer</p>
                <ul>
                    {trip.destinations.map((d) => (
                        <li key={d.id}>
                            {d.city}-{d.country}
                        </li>
                    ))}
                </ul>
            </section>

            <p>
                Startdatum: {formatDate(trip.startDate)} Slutdatum:{" "}
                {formatDate(trip.endDate)}
            </p>
            <section>
                <p>Aktiviteter</p>
            </section>
            <section>
                <p>Anteckningar</p>
            </section>
            <p>Skapad: {formatDate(trip.createdAt)}</p>
            <div>{/* Redigera och ta bort Link/knapp */}</div>
        </div>
    );
}
