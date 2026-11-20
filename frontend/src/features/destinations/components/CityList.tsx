import { useEffect, useState } from "react";

import type { City } from "../destination";
import { getCities } from "../DestinationApi";

type CityListProps = {
    countryId: number;
};

export function CityList({ countryId }: CityListProps) {
    const [cities, setCities] = useState<City[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    // The page remounts this component with a new key when the country changes,
    // so the initial state above is correct for each fetch.
    useEffect(() => {
        let cancelled = false;

        getCities(countryId)
            .then((data) => {
                if (!cancelled) setCities(data);
            })
            .catch((err) => {
                console.error(err);
                if (!cancelled) setError("Kunde inte hämta städer");
            })
            .finally(() => {
                if (!cancelled) setIsLoading(false);
            });

        // Ignore the response if the user picks another country first
        return () => {
            cancelled = true;
        };
    }, [countryId]);

    if (isLoading) {
        return <p className="text-lg text-[#1d4237]/70">Laddar städer...</p>;
    }

    if (error) {
        return (
            <div className="rounded-xl border border-red-200 bg-red-50 px-6 py-5">
                <p className="text-red-700">{error}</p>
            </div>
        );
    }

    if (cities.length === 0) {
        return (
            <div className="flex flex-col items-center gap-4 rounded-xl border border-dashed border-[#9fb0aa] px-6 py-10 text-center">
                <p className="text-lg text-[#1d4237]">
                    Det finns inga tillagda städer i detta land än
                </p>
            </div>
        );
    }

    return (
        <ul className="flex flex-col gap-3">
            {cities.map((city) => (
                <li
                    key={city.id}
                    className="rounded-xl border border-[#9fb0aa] bg-white px-6 py-5"
                >
                    <h3 className="text-lg font-semibold text-[#1d4237]">
                        {city.name}
                    </h3>
                    <p>
                       shortDescription
                        {/* {city.shortDescription} */}
                    </p>

                    <img /* className="city-image" src="{city.image}" */
                       
                    />
                </li>
            ))}
        </ul>
    );
}
