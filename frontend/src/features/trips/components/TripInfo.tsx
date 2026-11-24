import { useEffect, useMemo, useState } from "react";
import type { TripInfoType } from "../trips";
import type { Place } from "../../places/places";
import { getTripInfo, reorderTripDestinations, addTripDestination, addTripPlaceActivity } from "../TripFetch";
import { useParams } from "react-router";
import { formatDate } from "../../../shared/formatDate";
import { Link } from "react-router";
import { ApiError } from "../../../api/ApiClient";
import { useAuth } from "../../../context/AuthContext";
import { SentInvitations } from "../../invitations/SentInvitations";
import { TripActivities } from "../../activities/components/TripActivities";
import { TripNotes } from "../../trip-notes/components/TripNotes";
import { AddToTripModal } from "../../../shared/components/AddToTripModal";
import { getPlaces } from "../../places/PlaceFetch";
import { TripMembers } from "../../trip-members/TripMembers";
import { LeaveTripButton } from "./LeaveTripButton";
import { TripEvents } from "./TripEvents";

export function TripInfo({ tripId }: { tripId: number }) {
    const { user } = useAuth();
    const [error, setError] = useState("");
    const [reorderError, setReorderError] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [trip, setTrip] = useState<TripInfoType>();
    const [showModal, setShowModal] = useState(false);
    const [places, setPlaces] = useState<Place[]>([]);
    const [selectedPlace, setSelectedPlace] = useState<Place | null>(null)
    const [eventsVersion, setEventsVersion] = useState(0);


    useEffect(() => {
        getTripInfo(tripId)
            .then((data) => setTrip(data))
            .catch((err) => {
                console.error(err);
                if (err instanceof ApiError && err.status === 404) {
                    setError("resan hittades inte");
                } else if (err instanceof ApiError && err.status === 403) {
                    setError("Du har inte tillgång till resan");
                } else setError("Kunde inte hämta resan, försök igen");
            })
            .finally(() => setIsLoading(false));
    }, [tripId]);

    useEffect(() => {
    getPlaces()
        .then(setPlaces)
        .catch(console.error);
}, []);


    // useMemo keeps the array stable between renders
    // without it TripActivities gets a new array every time - endless loop of requests
    const destinationOptions = useMemo(
        () =>
            trip?.destinations.map((d) => ({ id: d.id, label: d.city, cityId: d.cityId })) ?? [],
        [trip],
    );

    if (isLoading) {
        return (
            <div className="rounded-xl border border-[#d6e1d9] bg-white px-6 py-8 text-[#526c62]">
                Laddar resa...
            </div>
        );
    }

    if (error)
        return (
            <div className="mx-auto max-w-3xl">
                <Link
                    to="/trips"
                    className="mb-5 inline-flex items-center text-sm font-medium text-[#235e4f] hover:text-[#1d4237] hover:underline"
                >
                    ← Tillbaka till mina resor
                </Link>
                <div
                    role="alert"
                    className="rounded-xl border border-red-200 bg-red-50 px-6 py-5 text-red-700"
                >
                    {error}
                </div>
            </div>
        );
    if (!trip) {
        return (
            <p className="rounded-xl border border-[#d6e1d9] bg-white px-6 py-8 text-[#526c62]">
                Resan finns inte
            </p>
        );
    }

    const moveDestination = async (index: number, direction: -1 | 1) => {
        const nextIndex = index + direction;
        if (!trip || nextIndex < 0 || nextIndex >= trip.destinations.length)
            return;
        const updated = [...trip.destinations];
        [updated[index], updated[nextIndex]] = [
            updated[nextIndex],
            updated[index],
        ];
        setTrip({ ...trip, destinations: updated });
        try {
            await reorderTripDestinations(
                trip.id,
                updated.map((destination) => destination.id)
            );
            setReorderError("");
        } catch (err) {
            console.error(err);
            setTrip({ ...trip, destinations: trip.destinations });
            setReorderError("Kunde inte ändra destinationernas ordning");
        }
    };

    // const handleAddDestination = async (cityId: number,) => {
    //     if (!trip) return;

    //     try {
    //         await addTripDestination(
    //             trip.id,
    //             cityId,
    //             trip.destinations.length,
    //         );

    //         const updatedTrip = await getTripInfo(trip.id);
    //         setTrip(updatedTrip);
    //     } catch (error) {
    //         console.error(error);
    //     }
    // };

    const handleAddPlace = async (tripId: number, placeId: number):Promise<void> => {
            if(!selectedPlace) return;

            await addTripPlaceActivity(
                tripId,
                placeId,
            );

            console.log("Aktivitet Skapad");

    }

    return (
        <div className="mx-auto max-w-4xl">
            <Link
                to="/trips"
                className="mb-5 inline-flex items-center text-sm font-medium text-[#235e4f] hover:text-[#1d4237] hover:underline"
            >
                ← Tillbaka till mina resor
            </Link>
            <Link to={`/trips/edit/${tripId}`}>redigera resa</Link>

            <article className="overflow-hidden rounded-2xl border border-[#d6e1d9] bg-white shadow-sm">
                <header className="border-b border-[#d6e1d9] bg-[#f8faf7] px-5 py-6 sm:px-8">
                    <p className="mb-1 text-sm font-medium uppercase tracking-wide text-[#526c62]">
                        Resedetaljer
                    </p>
                    <h1 className="text-3xl font-bold tracking-tight text-[#1d4237]">
                        {trip.title}
                    </h1>
                    <div className="mt-4 flex flex-wrap gap-x-6 gap-y-2 text-[#526c62]">
                        <p>
                            <span className="font-medium text-[#1d4237]">
                                Startdatum:
                            </span>{" "}
                            {formatDate(trip.startDate)}
                        </p>
                        <p>
                            <span className="font-medium text-[#1d4237]">
                                Slutdatum:
                            </span>{" "}
                            {formatDate(trip.endDate)}
                        </p>
                    </div>
                    {user?.id === trip.ownerId && <SentInvitations tripId={trip.id} />}
                    {user && user.id !== trip.ownerId && <LeaveTripButton tripId={trip.id} />}
                    {user?.id === trip.ownerId && (
                        <TripMembers key={trip.id} tripId={trip.id} onRemoved={() => setEventsVersion((current) => current + 1)} />
                    )}
                </header>

                <div className="grid gap-6 p-5 sm:p-8 lg:grid-cols-[1.1fr_0.9fr]">
                    <section>
                        <h2 className="mb-3 text-xl font-bold text-[#1d4237]">
                            Destinationer
                        </h2>
                        {reorderError && (
                            <p
                                role="alert"
                                className="mb-3 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700"
                            >
                                {reorderError}
                            </p>
                        )}
                        {trip.destinations.length === 0 ? (
                            <p className="rounded-lg border border-dashed border-[#9fb0aa] px-4 py-6 text-center text-[#526c62]">
                                Inga destinationer är tillagda ännu
                            </p>
                        ) : (
                            <ol className="flex flex-col gap-2">
                                {trip.destinations.map((d, index) => (
                                    <li
                                        key={d.id}
                                        className="flex items-center justify-between gap-3 rounded-lg border border-[#d6e1d9] px-4 py-3"
                                    >
                                        <div className="min-w-0 text-[#1d4237]">
                                            <span className="mr-2 inline-flex h-7 w-7 items-center justify-center rounded-full bg-[#e7efea] text-sm font-semibold">
                                                {index + 1}
                                            </span>
                                            <span className="font-medium">
                                                {d.city}
                                            </span>
                                            <span className="text-[#526c62]">
                                                , {d.country}
                                            </span>
                                        </div>
                                        <div className="flex shrink-0 gap-2">
                                            <button
                                                type="button"
                                                onClick={() =>
                                                    moveDestination(index, -1)
                                                }
                                                disabled={index === 0}
                                                aria-label={`Flytta ${d.city} uppåt`}
                                                className="h-10 w-10 rounded-lg border border-[#9fb0aa] text-[#1d4237] transition hover:border-[#235e4f] hover:bg-[#e7efea] disabled:cursor-not-allowed disabled:opacity-40"
                                            >
                                                🠉
                                            </button>
                                            <button
                                                type="button"
                                                onClick={() =>
                                                    moveDestination(index, 1)
                                                }
                                                disabled={
                                                    index ===
                                                    trip.destinations.length - 1
                                                }
                                                aria-label={`Flytta ${d.city} nedåt`}
                                                className="h-10 w-10 rounded-lg border border-[#9fb0aa] text-[#1d4237] transition hover:border-[#235e4f] hover:bg-[#e7efea] disabled:cursor-not-allowed disabled:opacity-40"
                                            >
                                                🠋
                                            </button>
                                        </div>
                                    </li>
                                ))}
                            </ol>
                        )}
                    </section>

                    <div className="flex flex-col gap-5">
                        <section className="rounded-xl border border-[#d6e1d9] p-5">
                            <h2 className="mb-2 text-lg font-bold text-[#1d4237]">
                                Aktiviteter
                            </h2>
                            <TripActivities
                                tripId={tripId}
                                destinations={destinationOptions}
                            />
                        </section>
                        <section className="rounded-xl border border-[#d6e1d9] p-5">
                            <h2 className="mb-2 text-lg font-bold text-[#1d4237]">
                                Anteckningar
                            </h2>
                            <TripNotes
                                tripId={tripId}
                                tripOwnerId={trip.ownerId}
                            />
                        </section>
                    </div>
                </div>

                <TripEvents key={trip.id} tripId={trip.id} refreshVersion={eventsVersion} />

                <footer className="border-t border-[#d6e1d9] px-5 py-4 text-sm text-[#526c62] sm:px-8">
                    Skapad: {formatDate(trip.createdAt)}
                </footer>

                {selectedPlace && (
                    <AddToTripModal
                        isOpen={showModal}
                        itemId={selectedPlace?.placeId ?? 0}
                        itemName={selectedPlace?.name ?? ""}
                        itemType="place"
                        onClose={() => setShowModal(false)}
                        onAdd={handleAddPlace}
                    />
                )}
            </article>
        </div>
    );
}
