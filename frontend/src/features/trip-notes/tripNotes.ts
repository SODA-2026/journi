export interface TripNote {
    id: number;
    content: string;
    createdAt: string;
}

export interface CreateTripNoteRequest {
    content: string;
}