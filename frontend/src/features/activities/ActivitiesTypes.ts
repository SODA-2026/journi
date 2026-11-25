
export interface CreateActivityRequest {
    tripDestinationId: number;
    name: string;
    activityDate: string; // "2027-07-03"
    startTime?: string;   // "20:00"
    description?: string;
}

export interface ActivityResponse {
    id: number;
    tripDestinationId: number;
    name:string;
    activityDate: string;
    startTime: string | null; // optional
    description: string | null;
    status: "PROPOSED" | "PLANNED" | "CANCELLED";¨
    createdById: number || null;
    createdByName: string || null;
}

export interface ActivityFormData{
    tripDestinationId: number;
    name: string;
    activityDate: string;
    startTime: string;
    description: string;
}

// ?
export type ActivityFormErrors = Partial<Record<keyof ActivityFormData, string>>;