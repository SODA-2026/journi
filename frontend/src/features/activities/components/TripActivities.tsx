// Parent component

import { useEffect, useState } from "react";
import type { ActivityResponse, UpdateActivityRequest } from "../ActivitiesTypes";
import { deleteActivity, getActivities, updateActivity } from "../ActivitiesApi";
import { ActivityForm } from "./ActivityForm";
import { ActivityList } from "./ActivityList";
import PlaceSuggestions from "../../places/components/PlaceSuggestions";

interface DestinationOption {
    id: number;
    label: string;
    cityId: number;
}

interface TripActivitiesProps {
    tripId: number;
    destinations: DestinationOption[];
}

export function TripActivities({ tripId, destinations }: TripActivitiesProps) {
    const [activities, setActivities] = useState<ActivityResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // Loading data during slow fetching
    useEffect(() => {
        let cancelled = false;
        refreshActivities();
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

    // Declared as a function so the effect above can call it even when
    // the component returns early for an empty destination list.
    async function refreshActivities() {
        try {
            const lists = await Promise.all(
                destinations.map((d) => getActivities(tripId, d.id)),
            );
            setActivities(lists.flat());
            setError("");
        } catch (err) {
            console.error("Failed to refresh activities:", err);
        }
    }

    async function handleUpdate(id: number, data: UpdateActivityRequest) {
        await updateActivity(id, data);
        await refreshActivities();
    }

    async function handleDelete(id: number) {
        await deleteActivity(id);
        await refreshActivities();
    }

    return (
        <div className="flex flex-col gap-5">
            <ActivityForm
                tripId={tripId}
                destinations={destinations}
                onCreated={refreshActivities}
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
                    onUpdate={handleUpdate}
                    onDelete={handleDelete}
                />
            )}

            {/* New suggestions of places for every destinations */}

            {destinations.map((d) => (
                <PlaceSuggestions key={d.id} cityId={d.cityId} cityName={d.label} tripId={tripId} onActivityAdded={refreshActivities}/>
            ))}
        </div>
    );
}
