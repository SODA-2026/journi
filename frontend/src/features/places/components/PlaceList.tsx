import { useEffect, useState } from "react";

import type { Place } from "../places";
import { getPlaces } from "../PlacesApi";

export function PlaceList() {
    const [places, setPlaces] = useState<Place[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        getPlaces()
            .then((data) => setPlaces(data))
            .catch((err) => {
                console.error(err);
                setError("Kunde inte hämta platser");
            })
            .finally(() => setIsLoading(false));
    }, []);

    if (isLoading) {
        return <p className="text-lg text-[#1d4237]/70">Laddar platser...</p>;
    }

    if (error) {
        return (
            <div className="rounded-xl border border-red-200 bg-red-50 px-6 py-5">
                <p className="text-red-700">{error}</p>
            </div>
        );
    }

    if (places.length === 0) {
        return (
            <div className="flex flex-col items-center gap-4 rounded-xl border border-dashed border-[#9fb0aa] px-6 py-10 text-center">
                <p className="text-lg text-[#1d4237]">Det finns inga platser än</p>
            </div>
        );
    }

    return (
        <div className="flex flex-col gap-4">
            <h2 className="text-2xl font-bold text-[#1d4237]">Alla platser</h2>
            <ul className="flex flex-col gap-3">
                {places.map((place) => (
                    <li
                        key={place.placeId}
                        className="rounded-xl border border-[#9fb0aa] bg-white px-6 py-5 transition hover:border-[#235e4f]"
                    >
                        <h3 className="text-lg font-semibold text-[#1d4237]">
                            {place.name}
                        </h3>
                        {place.shortDescription && (
                            <p className="text-sm text-[#1d4237]/70">
                                {place.shortDescription}
                            </p>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
}