export interface TripNote {
    id: number;
    content: string;
    createdAt: string;
    authorId: number | null;
    authorName: string | null;
}

export interface CreateTripNoteRequest {
    content: string;
}