import { useEffect, useState } from "react";
import type { Places } from "../PlacesTypes";
import { getPlacesByCityId } from "../PlacesApi";
import { data } from "react-router";
import PlaceCard from "./PlaceCard";

interface PlaceSuggestionsProps {
    cityId?: number | null;
    cityName: string;
    tripId: number;
    onActivityAdded: () => Promise<void>;
}

export default function PlaceSuggestions({ cityId, cityName, tripId, onActivityAdded }: PlaceSuggestionsProps) {
    const [places, setPlaces] = useState<Places[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!cityId) {
            setPlaces([]);
            return;
        }

        let cancelled = false;
        setLoading(true);
        setError(null);

        getPlacesByCityId(cityId)
            .then((data) => {
                if (!cancelled) setPlaces(data);
            })
            .catch(() => {
                if (!cancelled)
                    setError("Kunde inte hämta förslag. Försök igen.");
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });

        return() => {
            cancelled = true
        }
    }, [cityId]);

    return(
        <section>
            <h3 className="mb-1 text-lg font-bold text-[#1d4237]">Platser i {cityName}</h3>

            {!cityId && <p>Välj en destination för att se förslag</p>}
            {cityId && loading && <p>Hämtar förslag...</p>}
            {cityId && error && <p role="alert">{error} </p>}
            {cityId && !loading && !error && places.length === 0 && (
                <p>Inga förslag hittades för den här destination ännu.</p>
            )}

            {places.map((place) => (
                <PlaceCard key={place.placeId} place={place} tripId={tripId} onActivityAdded={onActivityAdded}></PlaceCard>
            ))}
            {/* Det ska kanske finnas ett knappt för att lägga till föreslagna aktiviteter här */}
        </section>
    )
}
