import type { Country, City } from "./destination";
const apiUrl = "http://localhost:8080";

export async function getCountries(): Promise<Country[]> {
    const response = await fetch(`${apiUrl}/countries`, {
        method: "GET",
    });
    if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
    }

    return response.json();
}

export async function getCities(countryId: number): Promise<City[]> {
    const response = await fetch(`${apiUrl}/cities/${countryId}`, {
        method: "GET",
    });
    if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
    }

    return response.json();
}
