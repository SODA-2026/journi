export interface TripNote {
    id: number;
    content: string;
    createdAt: string;
    authorId: number;
}

export interface CreateTripNoteRequest {
    content: string;
}