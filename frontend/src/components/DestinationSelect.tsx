import { useState } from "react";
import { countries, cities } from "../testDataSuggestion";
export function DestinationSelect() {
    const [countryId, setCountryId] = useState(0);
    function handleSelect(e: React.ChangeEvent<HTMLSelectElement>) {
        setCountryId(Number(e.target.value));
    }
    return (
        <>
            <select name="" id="" value={countryId} onChange={handleSelect}>
                <option value={0}>Välj Land</option>
                {countries.map((c) => (
                    <option key={c.countryId} value={c.countryId}>
                        {c.name}
                    </option>
                ))}
            </select>
            <p>valt: {countryId}</p>

            <select name="" id="">
                <option value="">Välj Stad</option>
                {cities
                    .filter((c) => c.countryId === countryId)
                    .map((c) => (
                        <option key={c.cityId} value={c.cityId}>
                            {c.name}
                        </option>
                    ))}
            </select>
        </>
    );
}
