import type {
    ActivityFormData,
    ActivityResponse,
    CreateActivityRequest,
} from "./ActivitiesTypes";
import { api } from "../../api/ApiClient";

const apiUrl = "http://localhost:8080/api/activities";

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
