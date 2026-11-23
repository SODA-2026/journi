import { useEffect, useState } from "react";

import { getCountries } from "../destinations/DestinationApi";
import { CityList } from "./components/CityList";
import { PageHeader } from "../../shared/PageHeader";

export function ExplorePage() {
    const [countryNames, setCountryNames] = useState<Record<number, string>>({});

    useEffect(() => {
        getCountries()
            .then((data) =>
                setCountryNames(Object.fromEntries(data.map((c) => [c.countryId, c.name])))
            )
            .catch((err) => console.error(err));
    }, []);

    return (
        <>
            <PageHeader
                title="Utforska"
                description="Upptäck städer och hitta inspiration till din nästa resa"
            />

            <CityList countryNames={countryNames} />
        </>
    );
}
