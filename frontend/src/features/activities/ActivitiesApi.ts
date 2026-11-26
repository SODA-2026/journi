import type {
    ActivityFormData,
    ActivityResponse,
    UpdateActivityRequest,
} from "./ActivitiesTypes";
import { api } from "../../api/ApiClient";

// export async function createActivity(tripId: number, data: CreateActivityRequest ) {
//     const res = await fetch(`${apiUrl}/trips/${tripId}`, {
//         method: "POST",
//         headers: { "Content-Type": "application/json"},
//         body: JSON.stringify({
//             ...data,
//             startTime: data.startTime || null,
//             description: data.description || null,
//         }),
//     });
//     if(!res.ok) throw new Error("Kunde inte spara aktiviteten");
//     return (await res.json()) as ActivityResponse;
// }

export function createActivity(
    tripId: number,
    data: ActivityFormData,
): Promise<ActivityResponse> {
    return api(`/api/activities/trips/${tripId}`, {
        method: "POST",
        body: JSON.stringify({
            ...data,
            startTime: data.startTime || null,
            description: data.description || null,
        }),
    });
}

// Ett anrop per resmål
// export async function getActivities(tripId: number, tripDestinationId: number) {
//     const res = await fetch(
//         `${apiUrl}/trips/${tripId}?tripDestinationId=${tripDestinationId}`,
//         { credentials: "include" },
//     );
//     if (!res.ok) throw new Error("Kunde inte hämta aktiviteter");
//     return (await res.json()) as ActivityResponse[];
// }

export function getActivities(
    tripId: number,
    tripDestinationId: number,
): Promise<ActivityResponse[]> {
    return api(
        `/api/activities/trips/${tripId}?tripDestinationId=${tripDestinationId}`,
    );
}

export function updateActivity(
    id: number,
    data: UpdateActivityRequest,
): Promise<ActivityResponse> {
    return api(`/api/activities/${id}`, {
        method: "PUT",
        body: JSON.stringify(data),
    });
}

export function deleteActivity(id: number): Promise<void> {
    return api(`/api/activities/${id}`, {
        method: "DELETE",
    });
}
