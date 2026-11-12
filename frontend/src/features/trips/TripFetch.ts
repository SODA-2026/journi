import { api } from "../../api/ApiClient";
import type {
    CreateTripRequest,
    TripInfoType,
    TripResponse,
    TripSummary,
} from "./trips";
export function createTrip(data: CreateTripRequest): Promise<TripResponse> {
    return api("/trips/create", { method: "POST", body: JSON.stringify(data) });
}
export function getMyTrips(): Promise<TripSummary[]> {
    return api("/trips/mine");
}

export function getTripInfo(tripId: number): Promise<TripInfoType> {
    return api(`/trips/mine/${tripId}`);
}
