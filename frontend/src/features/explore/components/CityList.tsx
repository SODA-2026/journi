import { useEffect, useState } from "react";
import type { City } from "../../destinations/destination";
import { getAllCities } from "../ExploreApi";
import { cityCover } from "../cityCovers";
import { AddToTripModal } from "../../../shared/components/AddToTripModal";
import { addTripDestination } from "../../trips/TripFetch";
import { getTripInfo } from "../../trips/TripFetch";
import { Link } from "react-router";

type CityListProps = {
    countryNames: Record<number, string>;
};

export function CityList({ countryNames }: CityListProps) {
    const [cities, setCities] = useState<City[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [selectedCity, setSelectedCity] = useState<City | null>(null);

    const handleAddCity = async (
        tripId: number,
        cityId: number
    ): Promise<void> => {
        const trip = await getTripInfo(tripId);

        await addTripDestination(tripId, cityId, trip.destinations.length);
    };

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
        <div>
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
                                alt={`${city.name}${
                                    countryName ? `, ${countryName}` : ""
                                }`}
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
                                    <p className="text-sm text-[#1d4237]/70">
                                        {countryName}
                                    </p>
                                )}
                                <p>{city.shortDescription}</p>
                                <div className="mt-4 flex flex-wrap gap-3">
                                    <button
                                        onClick={() => {
                                            setSelectedCity(city);
                                            setShowModal(true);
                                        }}
                                        className="inline-flex h-10 items-center justify-center rounded-lg border border-[#235e4f] bg-[#235e4f] px-4 text-white hover:bg-[#1d4237]"
                                    >
                                        Lägg till i resa
                                    </button>
                                    <Link
                                        to={`/cities/${city.id}`}
                                        className="inline-flex h-10 items-center justify-center rounded-lg border border-[#235e4f] px-4 text-[#235e4f] hover:bg-[#235e4f]/10"
                                    >
                                        Visa mer
                                        <span className="sr-only">
                                            {" "}
                                            om {city.name}
                                        </span>
                                    </Link>
                                </div>
                            </div>
                        </li>
                    );
                })}
            </ul>
            {selectedCity && (
                <AddToTripModal
                    isOpen={showModal}
                    itemId={selectedCity.id}
                    itemName={selectedCity.name}
                    itemType="city"
                    onClose={() => setShowModal(false)}
                    onAdd={handleAddCity}
                />
            )}
        </div>
    );
}
