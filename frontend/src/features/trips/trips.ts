// WHat backend needs (POST /trips/create)
export interface CreateTripRequest {
    title: string;
    startDate: string;
    endDate: string;
    countryId: number;
    cityId: number;
    destinations?: TripDestinationInput[];
}

export interface TripDestinationInput {
    cityId: number;
}

export interface TripResponse {
    title: string;
}

export type TripFormData = CreateTripRequest;

// export interface TripFormData {
//     title: string;
//     startDate: string;
//     endDate: string;
//     countryId: number | null; // before the user has chosen, there is no value to add, durin page-reload
//     cityId: number | null ;
// }

export interface TripFormErrors {
    title?: string;
    startDate?: string;
    endDate?: string;
    countryId?: string;
    cityId?: string;
}

export interface TripSummary {
    ownerId: number;
    ownerName: string;
    tripId: number;
    title: string;
    createdAt: string;
    startDate: string;
}

export interface TripDestination {
    id: number;
    city: string;
    country: string;
    cityId: number;
}

export interface TripInfoType {
    ownerId: number;
    ownerName: string;
    id: number;
    title: string;
    startDate: string;
    endDate: string;
    createdAt: string;
    destinations: TripDestination[];
}
