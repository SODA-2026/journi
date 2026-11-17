import { useEffect, useState } from "react";
import type { TripInfoType } from "../trips";
import { getTripInfo } from "../TripFetch";
import { useParams } from "react-router";
import { formatDate } from "../../../shared/formatDate";
import { Link } from "react-router";
import { ApiError } from "../../../api/ApiClient";

export function TripInfo() {
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [trip, setTrip] = useState<TripInfoType>();

    let params = useParams();
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
