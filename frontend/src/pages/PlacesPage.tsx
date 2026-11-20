import { PageHeader } from "../shared/PageHeader";
import { PlaceList } from "../features/places/components/PlaceList";

export function PlacesPage() {
    return (
        <>
            <PageHeader
                title="Platser"
                description="Utforska platser att besöka"
            />
            <PlaceList />
        </>
    );
}