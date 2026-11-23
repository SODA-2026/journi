import { api } from "../../api/ApiClient";

export interface TripMember {
    tripId: number;
    userId: number;
    name: string;
}

export function getTripMembers(tripId: number): Promise<TripMember[]> {
    return api(`/api/trips/${tripId}/members`);
}

export function removeTripMember(tripId: number, userId: number): Promise<void> {
    return api(`/api/trips/${tripId}/members/${userId}`, {
        method: "DELETE",
    });
}
