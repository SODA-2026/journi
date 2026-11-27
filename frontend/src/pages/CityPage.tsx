import { useEffect, useState } from "react";
import { CityInfo } from "../features/explore/components/CityInfo";
import { getCountries } from "../features/destinations/DestinationApi";

export function CityPage() {
    const [countryNames, setCountryNames] = useState<Record<number, string>>(
        {}
    );

    useEffect(() => {
        getCountries()
            .then((data) =>
                setCountryNames(
                    Object.fromEntries(data.map((c) => [c.countryId, c.name]))
                )
            )
            .catch((err) => console.error(err));
    }, []);

    return <CityInfo countryNames={countryNames}></CityInfo>;
}
