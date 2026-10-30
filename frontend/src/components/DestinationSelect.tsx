import { useEffect, useState } from "react";

import type { City, Country } from "../types/destination";
import { getCities, getCountries } from "../api/DestinationApi";

type DestinationSelectProps = {
    showErrors: boolean;
    onCountryChange: (countryId: number) => void;
    onCityChange: (cityId: number) => void;
};

export function DestinationSelect({
    showErrors,
    onCountryChange,
    onCityChange,
}: DestinationSelectProps) {
    const [cities, setCities] = useState<City[]>([]);
    const [countries, setCountries] = useState<Country[]>([]);
    const [countryId, setCountryId] = useState(0);
    const [cityId, setCityId] = useState(0);
    const countryError = countryId === 0 ? "Du måste välja ett land" : "";
    const cityError = cityId === 0 ? "Du måste välja en stad" : "";

    useEffect(() => {
        if (countryId === 0) {
            setCities([]);
            return;
        }
        getCities(countryId)
            .then((data) => setCities(data))
            .catch((error) => console.error(error));
    }, [countryId]);

    useEffect(() => {
        getCountries()
            .then((data) => setCountries(data))
            .catch((error) => console.error(error));
    }, []);

    function handleCitySelect(e: React.ChangeEvent<HTMLSelectElement>) {
        const newCityId = Number(e.target.value);
        setCityId(newCityId);
        onCityChange(newCityId);
    }
    function handleCountrySelect(e: React.ChangeEvent<HTMLSelectElement>) {
        const newCountryId = Number(e.target.value);
        setCountryId(newCountryId);
        onCountryChange(newCountryId);
        onCityChange(0);
        setCityId(0);
    }
    return (
        <div className="flex w-full flex-col gap-4">
            <div className="flex flex-col gap-2">
                <label htmlFor="country" className="text-lg text-[#1d4237]">
                    Land
                </label>
                <select
                    id="country"
                    value={countryId}
                    onChange={handleCountrySelect}
                    className="w-full rounded-lg border border-[#9fb0aa] bg-white px-5 py-3.5 text-lg text-[#1d4237] focus:border-[#235e4f] focus:outline-none focus:ring-2 focus:ring-[#235e4f]/20"
                >
                    <option value={0}>Välj land</option>
                    {countries.map((c) => (
                        <option key={c.countryId} value={c.countryId}>
                            {c.name}
                        </option>
                    ))}
                </select>
                {showErrors && countryError && (
                    <p className="text-sm text-red-700">{countryError}</p>
                )}
            </div>

            <div className="flex flex-col gap-2">
                <label htmlFor="city" className="text-lg text-[#1d4237]">
                    Stad
                </label>
                <select
                    id="city"
                    value={cityId}
                    onChange={handleCitySelect}
                    disabled={countryId === 0}
                    className="w-full rounded-lg border border-[#9fb0aa] bg-white px-5 py-3.5 text-lg text-[#1d4237] focus:border-[#235e4f] focus:outline-none focus:ring-2 focus:ring-[#235e4f]/20 disabled:cursor-not-allowed disabled:bg-gray-50 disabled:text-gray-400"
                >
                    <option value={0}>Välj stad</option>
                    {cities
                        .filter((c) => c.countryId === countryId)
                        .map((c) => (
                            <option key={c.cityId} value={c.cityId}>
                                {c.name}
                            </option>
                        ))}
                </select>
                {countryId === 0 ? (
                    <p className="text-sm text-gray-500">Välj land först</p>
                ) : (
                    showErrors &&
                    cityError && (
                        <p className="text-sm text-red-700">{cityError}</p>
                    )
                )}
            </div>
        </div>
    );
}
