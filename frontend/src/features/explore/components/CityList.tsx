import { useEffect, useState } from "react";

import type { City } from "../../destinations/destination";
import { getAllCities } from "../ExploreApi";
import { cityCover } from "../cityCovers";

type CityListProps = {
    countryNames: Record<number, string>;
};

export function CityList({ countryNames }: CityListProps) {
    const [cities, setCities] = useState<City[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let cancelled = false;

        getAllCities()
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

        return () => {
            cancelled = true;
        };
    }, []);

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
                    Det finns inga tillagda städer än
                </p>
            </div>
        );
    }

    return (
        <ul className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {cities.map((city) => {
                const countryName = countryNames[city.countryId];

                return (
                    <li
                        key={city.id}
                        className="overflow-hidden rounded-xl border border-[#9fb0aa] bg-white"
                    >
                        <img
                            src={cityCover(city, countryName)}
                            alt={`${city.name}${countryName ? `, ${countryName}` : ""}`}
                            loading="lazy"
                            className="h-40 w-full object-cover"
                            onError={(event) => {
                                event.currentTarget.style.display = "none";
                            }}
                        />
                        <div className="px-6 py-5">
                            <h3 className="text-lg font-semibold text-[#1d4237]">
                                {city.name}
                            </h3>
                            {countryName && (
                                <p className="text-sm text-[#1d4237]/70">{countryName}</p>
                            )}
                            <p>{city.shortDescription}</p>
                        </div>
                    </li>
                );
            })}
        </ul>
    );
}
