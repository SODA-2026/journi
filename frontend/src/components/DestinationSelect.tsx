import { useState } from "react";
import { countries, cities } from "../testDataSuggestion";
export function DestinationSelect() {
    const [countryId, setCountryId] = useState(0);
    const [cityId, setCityId] = useState(0);
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
