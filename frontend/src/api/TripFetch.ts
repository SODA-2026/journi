import type { CreateTripRequest, TripResponse } from '../types/trips';

export async function createTrip(
    data: CreateTripRequest,
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
