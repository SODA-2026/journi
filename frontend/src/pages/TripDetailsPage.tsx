import { useParams } from "react-router";
import { TripInfo } from "../features/trips/components/TripInfo";

export function TripDetailsPage() {
    const { id } = useParams();
    return <TripInfo key={id} tripId={Number(id)} />;
}
