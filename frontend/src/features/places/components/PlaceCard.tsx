import type { Places, PlaceCategory } from "../PlacesTypes";
import { addTripPlaceActivity } from "../../trips/TripFetch";

const CATEGORY_LABELS: Record<PlaceCategory, string> = {
    TOURIST_ATTRACTION: 'Sevärdhet',
    FOOD: 'Mat',
    BAR: 'Bar',
    FAMILY: 'Familj',
    SPORT: 'Sport',
}

interface PlaceCardProps {
    place: Places;
    tripId: number;
    onActivityAdded: () => Promise<void>;
}

export default function PlaceCard({place, tripId, onActivityAdded}: PlaceCardProps ) {   

    const handleAddPlace = async () => {
            await addTripPlaceActivity(
                tripId,
                place.placeId,
            );

            await onActivityAdded();
            console.log("Aktivitet Skapad");
    }

    return(
            <div className="place-card flex flex-col mb-4 border-b">
                <div className="place-card_header flex items-center gap-2">
                    <strong >{place.name} </strong>
                    <span className="place-card_category ml-2 text-gray-600 italic">
                        - {CATEGORY_LABELS[place.category] ?? place.category}
                    </span>
                    <button onClick={handleAddPlace} className="ml-auto rounded-lg bg-[#235e4f] px-3 py-1 text-white hover:bg-[#1d4237]">
                        +
                    </button>
                </div>

                <p className="place-card_discription">
                    {place.shortDescription}
                </p>
                
            </div>
    )

}