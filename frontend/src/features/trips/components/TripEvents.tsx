import { useEffect, useState } from "react";
import { getTripEvents, type TripEvent } from "../TripFetch";
import { useAuth } from "../../../context/AuthContext";

type TripEventsProps = {
    tripId: number;
    refreshVersion?: number;
};

export function TripEvents({ tripId, refreshVersion = 0 }: TripEventsProps) {
    const { user } = useAuth();
    const [events, setEvents] = useState<TripEvent[] | null>(null);
    const [hasError, setHasError] = useState(false);

    useEffect(() => {
        let active = true;
        let latestRequest = 0;

        function refresh() {
            const request = ++latestRequest;
            getTripEvents(tripId)
                .then((data) => {
                    if (active && request === latestRequest) {
                        setEvents(data);
                        setHasError(false);
                    }
                })
                .catch(() => {
                    if (active && request === latestRequest) setHasError(true);
                });
        }

        refresh();
        window.addEventListener("focus", refresh);
        return () => {
            active = false;
            window.removeEventListener("focus", refresh);
        };
    }, [tripId, refreshVersion]);

    function eventMessage(event: TripEvent) {
        if (event.eventType === "MEMBER_JOINED") {
            return event.memberId === user?.id
                ? "Du gick med i resan."
                : `${event.memberName} gick med i resan.`;
        }
        if (event.eventType === "MEMBER_REMOVED") {
            return event.actorId === user?.id
                ? `Du tog bort ${event.memberName} från resan.`
                : `${event.actorName ?? "Resans ägare"} tog bort ${event.memberName} från resan.`;
        }
        return `${event.memberName} har lämnat resan.`;
    }

    return (
        <section aria-label="Händelser" className="border-t border-[#d6e1d9] px-5 py-5 sm:px-8">
            <h2 className="mb-3 text-lg font-bold text-[#1d4237]">Händelser</h2>
            {hasError && (
                <p role="alert" className="text-red-700">
                    Kunde inte hämta händelser. Ladda om sidan för att försöka igen.
                </p>
            )}
            {!hasError && events === null && <p>Laddar händelser...</p>}
            {!hasError && events?.length === 0 && (
                <p className="text-[#526c62]">Inga händelser ännu.</p>
            )}
            {!!events?.length && (
                <ul className="max-h-64 space-y-3 overflow-y-auto" aria-live="polite" aria-relevant="additions">
                    {events.map((event) => (
                        <li key={event.id} className="rounded-lg bg-[#f8faf7] px-4 py-3">
                            <p className="text-[#1d4237]">{eventMessage(event)}</p>
                            <time dateTime={event.createdAt} className="text-sm text-[#526c62]">
                                {new Date(event.createdAt).toLocaleString("sv-SE", {
                                    dateStyle: "medium",
                                    timeStyle: "short",
                                })}
                            </time>
                        </li>
                    ))}
                </ul>
            )}
        </section>
    );
}
