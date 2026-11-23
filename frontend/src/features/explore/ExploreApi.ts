import { api } from "../../api/ApiClient";
import type { City } from "../destinations/destination";

export function getAllCities(): Promise<City[]> { return api("/cities"); }
