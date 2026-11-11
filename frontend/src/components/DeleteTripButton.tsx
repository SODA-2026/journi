import { deleteTrip } from "../api/TripFetch"

type DeleteTripButtonProps = {
    tripId: number;
    onDeleted: () => void;
};

export function DeleteTripButton({
    tripId,
    onDeleted,
}: DeleteTripButtonProps) {
  async function handleDelete() {
    const confirmed = window.confirm(
          "Are you sure you want to delete this trip?"
        );

        if (!confirmed) {
            return;
        }

        try {
            await deleteTrip(tripId);
            onDeleted();
        } catch (error) {
            console.error("Failed to delete trip:", error);
            window.alert("Could not delete the trip.");
        }
    }

  return (
        <button type="button" onClick={handleDelete}>
            Delete
        </button>
    );
}