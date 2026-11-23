import type { Place } from "./places";
import { api } from "../../api/ApiClient";

export function getPlaces(): Promise<Place[]> {
    return api("/places");
}