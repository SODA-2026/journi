import type { City, Country } from "./types/destination";

export const countries: Country[] = [
    { countryId: 1, name: "Sverige" },
    { countryId: 2, name: "Norge" },
    { countryId: 3, name: "Danmark" },
];
export const cities: City[] = [
    { cityId: 4, countryId: 1, name: "Malmö" },
    { cityId: 5, countryId: 2, name: "Oslo" },
    { cityId: 6, countryId: 3, name: "Köpenhamn" },
    { cityId: 10, countryId: 1, name: "Göteborg" },
    { cityId: 11, countryId: 2, name: "Bergen" },
    { cityId: 12, countryId: 3, name: "Aarhus" },
];
