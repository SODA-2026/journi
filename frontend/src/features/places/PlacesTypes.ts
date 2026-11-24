export type PlaceCategory =
    | "TOURIST_ATTRACTION"
    | "FOOD"
    | "BAR"
    | "FAMILY"
    | "SPORT";

export interface Places {
    placeId: number;
    name: string;
    shortDescription: string | null;
    cityId: string;
    category: PlaceCategory;
}
