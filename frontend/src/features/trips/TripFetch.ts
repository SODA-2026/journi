import type {
    CreateTripRequest,
    TripResponse,
    TripSummary,
} from "./trips";
const apiUrl = "http://localhost:8080/trips";

export async function createTrip(
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
}

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
    const response = await fetch(`http://localhost:8080/trips/${id}`, {
        method: "DELETE",
    });

    if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
    }
}
export function getMyTrips(): Promise<TripSummary[]> { return api("/trips/mine"); }
