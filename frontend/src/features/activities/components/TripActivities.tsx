// Parent component

import { useEffect, useState } from "react";
import type { ActivityResponse } from "../ActivitiesTypes";
import { getActivities } from "../ActivitiesApi";
import { ActivityForm } from "./ActivityForm";
import { ActivityList } from "./ActivityList";

interface DestinationOption {
    id: number;
    label: string;
}

interface TripActivitiesProps {
    tripId: number;
    destinations: DestinationOption[];
    tripStartDate: string;
    tripEndDate: string;
}

export function TripActivities({ tripId, destinations, tripStartDate, tripEndDate }: TripActivitiesProps) {
    const [activities, setActivities] = useState<ActivityResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // Loading data during slow fetching
    useEffect(() => {
        let cancelled = false;

        Promise.all(destinations.map((d) => getActivities(tripId, d.id)))
            .then((lists) => {
                if (!cancelled) setActivities(lists.flat());
            })
            .catch((err) => {
                console.error(err);
                if (!cancelled) setError("Kunde inte hämta aktiviteter");
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [tripId, destinations]);

    if (destinations.length === 0) {
        return (
            <p className="rounded-xl border border-dashed border-[#9fb0aa] px-4 py-6 text-center text-sm text-[#526c62]">
                Lägg till ett resmål innan du skapar aktiviteter
            </p>
        );
    }

    return (
        <div className="flex flex-col gap-5">
            <ActivityForm
                tripId={tripId}
                destinations={destinations}
                tripStartDate={tripStartDate}
                tripEndDate={tripEndDate}
                onCreated={async () => {
                    // Refetch all activities after creation
                    const lists = await Promise.all(
                        destinations.map((d) => getActivities(tripId, d.id)),
                    );
                    setActivities(lists.flat());
                    setError("");
                }}
            />

            {loading ? (
                <p className="text-sm text-[#1d4237]/70">
                    Laddar aktiviteter...
                </p>
            ) : error ? (
                <p
                    role="alert"
                    className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700"
                >
                    {error}
                </p>
            ) : (
                <ActivityList
                    activities={activities}
                    destinations={destinations}
                />
            )}
        </div>
    );
}
