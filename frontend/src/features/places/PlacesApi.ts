
import { api } from "../../api/ApiClient";
import type { Places } from "./PlacesTypes";


export async function getPlacesByCityId(cityId: number): Promise<Places[]> {
    return api(`/api/places/city/${cityId}`)
}