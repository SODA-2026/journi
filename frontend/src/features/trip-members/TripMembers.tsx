import { useEffect, useRef, useState } from "react";
import { ApiError } from "../../api/ApiClient";
import { getTripMembers, removeTripMember, type TripMember } from "./TripMemberApi";

type TripMembersProps = {
    tripId: number;
    onRemoved?: () => void;
};

export function TripMembers({ tripId, onRemoved }: TripMembersProps) {
    const [members, setMembers] = useState<TripMember[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");
    const [removingMemberId, setRemovingMemberId] = useState<number | null>(null);
    const removalInProgress = useRef(false);

    useEffect(() => {
        let active = true;
        getTripMembers(tripId)
            .then((data) => {
                if (active) setMembers(data);
            })
            .catch(() => {
                if (active) setError("Kunde inte hämta deltagarna. Ladda om sidan för att försöka igen.");
            })
            .finally(() => {
                if (active) setIsLoading(false);
            });
        return () => {
            active = false;
        };
    }, [tripId]);

    async function handleRemove(member: TripMember) {
        if (removalInProgress.current) return;
        const confirmed = window.confirm(
            `Vill du ta bort ${member.name} från resan? ` +
            "Deltagaren förlorar åtkomsten. Resans innehåll finns kvar.",
        );
        if (!confirmed) return;

        removalInProgress.current = true;
        setRemovingMemberId(member.userId);
        setError("");
        setMessage("");
        try {
            await removeTripMember(tripId, member.userId);
            setMembers((current) => current.filter((item) => item.userId !== member.userId));
            setMessage(`${member.name} har tagits bort från resan.`);
            onRemoved?.();
        } catch (err) {
            setError(
                err instanceof ApiError && err.status === 403
                    ? "Endast resans ägare kan ta bort deltagare."
                    : "Kunde inte ta bort deltagaren. Försök igen eller ladda om sidan.",
            );
        } finally {
            removalInProgress.current = false;
            setRemovingMemberId(null);
        }
    }

    return (
        <section className="mt-5 rounded-xl border border-[#d6e1d9] bg-white p-4" aria-label="Deltagare">
            <h2 className="text-lg font-bold text-[#1d4237]">Deltagare</h2>
            {error && (
                <p role="alert" className="mt-3 text-red-700">{error}</p>
            )}
            {message && (
                <p role="status" className="mt-3 text-[#235e4f]">{message}</p>
            )}
            {isLoading && <p>Laddar deltagare...</p>}
            {!isLoading && !error && members.length === 0 && (
                <p>Inga deltagare ännu.</p>
            )}
            <ul className="mt-3 space-y-2">
                {members.map((member) => (
                    <li key={member.userId} className="flex items-center justify-between gap-3">
                        <span>{member.name}</span>
                        <button
                            type="button"
                            disabled={removingMemberId !== null}
                            onClick={() => handleRemove(member)}
                            aria-label={`Ta bort ${member.name}`}
                            className="rounded-lg border border-red-200 px-3 py-2 text-sm text-red-700 hover:bg-red-50 disabled:opacity-50"
                        >
                            {removingMemberId === member.userId ? "Tar bort..." : "Ta bort"}
                        </button>
                    </li>
                ))}
            </ul>
        </section>
    );
}
