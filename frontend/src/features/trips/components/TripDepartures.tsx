import { useCallback, useEffect, useRef, useState } from "react";
import { getTripDepartures, type TripDeparture } from "../TripFetch";

type TripDeparturesProps = {
    tripId: number;
};

export function TripDepartures({ tripId }: TripDeparturesProps) {
    const [departures, setDepartures] = useState<TripDeparture[] | null>(null);
    const [hasError, setHasError] = useState(false);
    const currentRequest = useRef(0);

    const refresh = useCallback(async () => {
        const request = ++currentRequest.current;
        try {
            const data = await getTripDepartures(tripId);
            if (request === currentRequest.current) {
                setDepartures(data);
                setHasError(false);
            }
        } catch {
            if (request === currentRequest.current) setHasError(true);
        }
    }, [tripId]);

    useEffect(() => {
        void refresh();
        window.addEventListener("focus", refresh);
        return () => {
            currentRequest.current++;
            window.removeEventListener("focus", refresh);
        };
    }, [refresh]);

    return (
        <section aria-label="Händelser" className="border-t border-[#d6e1d9] px-5 py-5 sm:px-8">
            <h2 className="mb-3 text-lg font-bold text-[#1d4237]">Händelser</h2>
            {hasError && <p role="alert" className="text-red-700">Kunde inte hämta händelser. Ladda om sidan för att försöka igen.</p>}
            {!hasError && departures === null && <p>Laddar händelser...</p>}
            {!hasError && departures?.length === 0 && <p className="text-[#526c62]">Ingen har lämnat resan ännu.</p>}
            {!!departures?.length && (
                <ul className="max-h-64 space-y-3 overflow-y-auto" aria-live="polite" aria-relevant="additions">
                    {departures.map((departure) => (
                        <li key={departure.id} className="rounded-lg bg-[#f8faf7] px-4 py-3">
                            <p className="text-[#1d4237]">{departure.memberName} har lämnat resan.</p>
                            <time dateTime={departure.createdAt} className="text-sm text-[#526c62]">
                                {new Date(departure.createdAt).toLocaleString("sv-SE", { dateStyle: "medium", timeStyle: "short" })}
                            </time>
                        </li>
                    ))}
                </ul>
            )}
        </section>
    );
}
