import { deleteTrip } from "../TripFetch";

type DeleteTripButtonProps = {
    tripId: number;
    onDeleted: (tripId: number) => void;
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
            onDeleted(tripId);
        } catch (error) {
            console.error("Failed to delete trip:", error);
            window.alert("Could not delete the trip.");
        }
    }

  return (
        <button
            type="button"
            onClick={handleDelete}
            className="mt-4 rounded-lg bg-red-600 px-4 py-2 text-white hover:bg-red-700"
        >
            Delete
        </button>
    );
}