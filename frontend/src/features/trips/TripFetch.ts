import { api } from "../../api/ApiClient";
import type {
    CreateTripRequest,
    TripInfoType,
    TripResponse,
    TripSummary,
    TripUpdateRequest,
} from "./trips";

export function getTripInfo(tripId: number): Promise<TripInfoType> {
    return api(`/trips/mine/${tripId}`);
}

const apiUrl = "http://localhost:8080/trips";

export function leaveTrip(tripId: number): Promise<void> {
    return api(`/api/trips/${tripId}/members/me`, { method: "DELETE" });
}

export interface TripEvent {
    id: number;
    memberName: string;
    createdAt: string;
    eventType: "MEMBER_JOINED" | "MEMBER_LEFT" | "MEMBER_REMOVED";
    actorId: number | null;
    actorName: string | null;
    memberId: number | null;
}

export function getTripEvents(tripId: number): Promise<TripEvent[]> {
    return api(`/api/trips/${tripId}/members/events`);
}
  
export function createTrip(data: CreateTripRequest): Promise<TripResponse> {
    return api("/trips/create", { method: "POST", body: JSON.stringify(data) });
}

export function updateTripFetch(
    tripId: number,
    data: TripUpdateRequest
): Promise<TripUpdateRequest> {
    return api(`/trips/mine/${tripId}`, {
        method: "PUT",
        body: JSON.stringify(data),
    });
}
/* export async function createTrip(
    data: CreateTripRequest
): Promise<TripResponse> {
    const response = await fetch("http://localhost:8080/trips/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
    });

    if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
    }

    return response.json();
} */
export async function getTripsByOwner(ownerId: number): Promise<TripSummary[]> {
    const response = await fetch(`${apiUrl}/${ownerId}`, {
        method: "GET",
    });
    if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
    }
    return response.json();
}
export async function deleteTrip(id: number): Promise<void> {
    return api(`/trips/${id}`, { method: "DELETE" });

    /* const response = await fetch(`http://localhost:8080/trips/${id}`, {
         method: "DELETE",
     });

     if (!response.ok) {
         throw new Error(`Server error: ${response.status}`);
     }

     */
}
export function getMyTrips(): Promise<TripSummary[]> {
    return api("/trips/mine");
}

export function reorderTripDestinations(
    tripId: number,
    destinationIds: number[]
): Promise<unknown> {
    return api(`/api/trips/${tripId}/destinations/reorder`, {
        method: "PUT",
        body: JSON.stringify(destinationIds),
    });
}

export function addTripDestination(
    tripId: number,
    cityId: number,
    orderIndex: number,
): Promise<unknown> {
    return api(`/api/trips/${tripId}/destinations`, {
        method: "POST",
        body: JSON.stringify({
            cityId,
            orderIndex,
        }),
    });
}

export function deleteTripDestination(
    tripId: number,
    destinationId: number,
): Promise<void> {
    return api(`/api/trips/${tripId}/destinations/${destinationId}`, {
        method: "DELETE",
    });
}

export function addTripPlaceActivity(
    tripId: number,
    placeId: number,
): Promise<void> {
    return api(`/api/activities/trips/${tripId}/places`, {
        method: "POST",
        body: JSON.stringify({
            placeId,
        }),
    })
}
