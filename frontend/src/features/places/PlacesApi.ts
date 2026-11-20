import { api } from "../../api/ApiClient";
import type { Place } from "./places";

export function getPlaces(): Promise<Place[]> {
    return api("/places");
}