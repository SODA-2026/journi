import { useRef, useState } from "react";
import { useNavigate } from "react-router";
import { ApiError } from "../../../api/ApiClient";
import { leaveTrip } from "../TripFetch";

type LeaveTripButtonProps = {
    tripId: number;
};

export function LeaveTripButton({ tripId }: LeaveTripButtonProps) {
    const navigate = useNavigate();
    const [isLeaving, setIsLeaving] = useState(false);
    const [error, setError] = useState("");
    const leaveInProgress = useRef(false);

    async function handleLeave() {
        if (leaveInProgress.current) return;
        const confirmed = window.confirm(
            "Vill du lämna resan? Du förlorar åtkomsten, " +
            "men dina anteckningar och aktiviteter finns kvar för de andra deltagarna.",
        );
        if (!confirmed) return;

        leaveInProgress.current = true;
        setIsLeaving(true);
        setError("");

        try {
            await leaveTrip(tripId);
            navigate("/trips", { replace: true });
        } catch (err) {
            setError(
                err instanceof ApiError && err.status === 400
                    ? "Du kan inte lämna en resa som du själv har skapat."
                    : "Kunde inte lämna resan. Försök igen eller ladda om sidan.",
            );
        } finally {
            leaveInProgress.current = false;
            setIsLeaving(false);
        }
    }

    return (
        <div className="mt-5">
            <button
                type="button"
                disabled={isLeaving}
                onClick={handleLeave}
                className="rounded-lg border border-red-200 px-4 py-2 text-sm font-medium text-red-700 hover:bg-red-50 disabled:cursor-not-allowed disabled:opacity-50"
            >
                {isLeaving ? "Lämnar resan..." : "Lämna resan"}
            </button>
            {error && (
                <p role="alert" className="mt-3 text-red-700">{error}</p>
            )}
        </div>
    );
}
