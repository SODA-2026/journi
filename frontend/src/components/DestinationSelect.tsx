import { useEffect, useState } from "react";

import type { City, Country } from "../types/destination";
import { getCities, getCountries } from "../api/DestinationApi";

export function DestinationSelect() {
    const [cities, setCities] = useState<City[]>([]);
    const [countries, setCountries] = useState<Country[]>([]);
    const [countryId, setCountryId] = useState(0);
    const [cityId, setCityId] = useState(0);

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
        setCityId(Number(e.target.value));
    }
    function handleCountrySelect(e: React.ChangeEvent<HTMLSelectElement>) {
        setCountryId(Number(e.target.value));
        setCityId(0);
    }
    return (
        <>
            <select
                name=""
                id=""
                value={countryId}
                onChange={handleCountrySelect}
            >
                <option value={0}>Välj Land</option>
                {countries.map((c) => (
                    <option key={c.countryId} value={c.countryId}>
                        {c.name}
                    </option>
                ))}
            </select>
            <p>valt: {countryId}</p>

            <select
                name=""
                id=""
                value={cityId}
                onChange={handleCitySelect}
                disabled={countryId === 0}
            >
                <option value={0}>Välj Stad</option>
                {cities
                    .filter((c) => c.countryId === countryId)
                    .map((c) => (
                        <option key={c.cityId} value={c.cityId}>
                            {c.name}
                        </option>
                    ))}
            </select>
            <p>valt: {cityId}</p>
        </>
    );
}
