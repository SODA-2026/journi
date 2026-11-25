import type { Places, PlaceCategory } from "../PlacesTypes";

const CATEGORY_LABELS: Record<PlaceCategory, string> = {
    TOURIST_ATTRACTION: 'Sevärdhet',
    FOOD: 'Mat',
    BAR: 'Bar',
    FAMILY: 'Familj',
    SPORT: 'Sport',
}

interface PlaceCardProps {
    place: Places
}

export default function PlaceCard({place}: PlaceCardProps ) {

    return(
        <div className="place-card">
            <div className="place-card_header">
                <strong >{place.name} </strong>
                <span className="place-card_category">
                    {CATEGORY_LABELS[place.category] ?? place.category}
                </span>
            </div>

            <p className="place-card_discription">
                {place.shortDescription}
            </p>

        </div>
    )

}