import type { Country } from "../types/destination";
const apiUrl = "http://localhost:8080/suggestions";

export async function getCountries(): Promise<Country[]> {
    const response = await fetch(`${apiUrl}/countries`, {
        method: "GET",
    });
    if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
    }

    return response.json();
}
