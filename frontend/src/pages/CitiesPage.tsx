import { useEffect, useState } from "react";

import type { Country } from "../features/destinations/destination";
import { getCountries } from "../features/destinations/DestinationApi";
import { CityList } from "../features/destinations/components/CityList";
import { PageHeader } from "../shared/PageHeader";

export function CitiesPage() {
    const [countries, setCountries] = useState<Country[]>([]);
    const [countryId, setCountryId] = useState(0);
    const [error, setError] = useState("");

    useEffect(() => {
        getCountries()
            .then((data) => setCountries(data))
            .catch((err) => {
                console.error(err);
                setError("Kunde inte hämta länder");
            });
    }, []);

    return (
        <>
            <PageHeader
                title="Städer"
                description="Välj ett land för att se dess städer"
            />

            {error && (
                <div className="mb-6 rounded-xl border border-red-200 bg-red-50 px-6 py-5">
                    <p className="text-red-700">{error}</p>
                </div>
            )}

            <div className="mb-6 flex flex-col gap-2">
                <label htmlFor="country" className="text-lg text-[#1d4237]">
                    Land
                </label>
                <select
                    id="country"
                    value={countryId}
                    onChange={(e) => setCountryId(Number(e.target.value))}
                    className="w-full rounded-lg border border-[#9fb0aa] bg-white px-5 py-3.5 text-lg text-[#1d4237] focus:border-[#235e4f] focus:outline-none focus:ring-2 focus:ring-[#235e4f]/20"
                >
                    <option value={0}>Välj land</option>
                    {countries.map((country) => (
                        <option key={country.countryId} value={country.countryId}>
                            {country.name}
                        </option>
                    ))}
                </select>
            </div>

            {countryId === 0 ? (
                <p className="text-gray-500">
                    Välj ett land för att se dess städer
                </p>
            ) : (
                <CityList key={countryId} countryId={countryId} />
            )}
        </>
    );
}
