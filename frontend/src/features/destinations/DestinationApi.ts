import { api } from "../../api/ApiClient";
import type { Country, City } from "./destination";
export function getCountries(): Promise<Country[]> { return api("/countries"); }
export function getCities(countryId: number): Promise<City[]> { return api(`/cities/${countryId}`); }
