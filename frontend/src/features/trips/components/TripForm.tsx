import { useRef, useState, type FormEvent } from "react";
import { createTrip } from "../TripFetch.ts";
import { validate } from "../TripValidation.ts";
import type { TripFormData, TripFormErrors } from "../trips.ts";
import { DestinationSelect } from "../../destinations/DestinationSelect.tsx";

const emptyForm: TripFormData = {
    title: "",
    startDate: "",
    endDate: "",
    countryId: 0,
    cityId: 0,
    destinations: [],
};

export function TripForm() {
    const [formData, setFormData] = useState<TripFormData>({ ...emptyForm });
    const [errors, setErrors] = useState<TripFormErrors>({});
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    const [showErrors, setShowErrors] = useState(false);
    const [resetKey, setResetKey] = useState(0);
    const [selectedDestination, setSelectedDestination] = useState<{
        countryId: number;
        cityId: number;
        countryName: string;
        cityName: string;
    } | null>(null);
    const [destinations, setDestinations] = useState<
        Array<{ countryId: number; cityId: number; countryName: string; cityName: string }>
    >([]);

    // More security than disabled:submitting in
    // Prevents multiple requests while a request is in progress.
    const requestInProgress = useRef(false);

    const clearMessages = () => {
        setSuccessMessage("");
        setSubmitError("");
    };

    // const [countryId, setCountryId] = useState(0);
    // const [cityId, setCityId] = useState(0);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;

        clearMessages();

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));

        setErrors((prev) => ({
            ...prev,
            [name]: undefined,
        }));
    };

    const handleCountryChange = (countryId: number) => {
        clearMessages();
        setSelectedDestination(null);

        setFormData((prev) => ({
            ...prev,
            countryId,
            cityId: 0,
        }));
    };

    const handleCityChange = (cityId: number) => {
        clearMessages();

        setFormData((prev) => ({
            ...prev,
            cityId,
        }));
    };

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (requestInProgress.current) {
            return;
        }

        clearMessages();
        setShowErrors(true);

        const dataToSubmit: TripFormData = {
            ...formData,
            title: formData.title.trim(),
            destinations: destinations.map(({ cityId }) => ({ cityId })),
        };

        const newErrors = validate(dataToSubmit);
        setErrors(newErrors);

        if (Object.keys(newErrors).length > 0) {
            return;
        }

        requestInProgress.current = true;
        setSubmitting(true);

        try {
            const savedTrip = await createTrip(dataToSubmit);
            setSuccessMessage(`Trip "${savedTrip.title}" created!`);
            setFormData({ ...emptyForm });
            setDestinations([]);
            setSelectedDestination(null);
            setErrors({});
            setShowErrors(false);

            // Resets the internal state of DestinationSelect.
            setResetKey((prev) => prev + 1);
        } catch (err) {
            console.error("Failed to create trip:", err);
            setSubmitError("Could not create the trip. Please try again.");
        } finally {
            requestInProgress.current = false;
            setSubmitting(false);
        }

        // console.log(formData);
    };

    const addDestination = () => {
        if (!selectedDestination || selectedDestination.cityId === 0) return;
        if (destinations.some((destination) => destination.cityId === selectedDestination.cityId)) return;
        setDestinations((prev) => [...prev, selectedDestination]);
        setSelectedDestination(null);
        setResetKey((prev) => prev + 1);
    };

    const removeDestination = (cityId: number) => {
        setDestinations((prev) => prev.filter((destination) => destination.cityId !== cityId));
    };

    const moveDestination = (index: number, direction: -1 | 1) => {
        const nextIndex = index + direction;
        if (nextIndex < 0 || nextIndex >= destinations.length) return;
        setDestinations((prev) => {
            const updated = [...prev];
            [updated[index], updated[nextIndex]] = [updated[nextIndex], updated[index]];
            return updated;
        });
    };

    return (
        <div className="mx-auto max-w-3xl">
            <header className="mb-8">
                <h1 className="text-3xl font-bold tracking-tight text-[#1d4237]">Skapa en resa</h1>
                <p className="mt-2 text-[#526c62]">Planera din resa och lägg till destinationer i önskad ordning.</p>
            </header>

            <form
                onSubmit={handleSubmit}
                noValidate
                className="rounded-2xl border border-[#d6e1d9] bg-white p-5 shadow-sm sm:p-8"
            >
                <fieldset disabled={submitting} className="flex flex-col gap-6">
                    <div className="flex flex-col gap-2">
                        <label htmlFor="title" className="text-lg font-medium text-[#1d4237]">Titel</label>
                        <input
                            type="text"
                            name="title"
                            id="title"
                            value={formData.title}
                            onChange={handleChange}
                            aria-invalid={Boolean(errors.title)}
                            aria-describedby={errors.title ? "title-error" : undefined}
                            className="w-full rounded-lg border border-[#9fb0aa] bg-white px-5 py-3.5 text-lg text-[#1d4237] placeholder:text-[#526c62]/60 focus:border-[#235e4f] focus:outline-none focus:ring-2 focus:ring-[#235e4f]/20"
                            placeholder="Till exempel Vårsemester i Italien"
                        />
                        {errors.title && <p id="title-error" className="text-sm text-red-700">{errors.title}</p>}
                    </div>

                    <div className="grid gap-5 sm:grid-cols-2">
                        <div className="flex flex-col gap-2">
                            <label htmlFor="startDate" className="text-lg font-medium text-[#1d4237]">Startdatum</label>
                            <input
                                type="date"
                                name="startDate"
                                id="startDate"
                                value={formData.startDate}
                                onChange={handleChange}
                                aria-invalid={Boolean(errors.startDate)}
                                aria-describedby={errors.startDate ? "startDate-error" : undefined}
                                className="w-full rounded-lg border border-[#9fb0aa] bg-white px-5 py-3.5 text-lg text-[#1d4237] focus:border-[#235e4f] focus:outline-none focus:ring-2 focus:ring-[#235e4f]/20"
                            />
                            {errors.startDate && (
                                <p id="startDate-error" className="text-sm text-red-700">{errors.startDate}</p>
                            )}
                        </div>

                        <div className="flex flex-col gap-2">
                            <label htmlFor="endDate" className="text-lg font-medium text-[#1d4237]">Slutdatum</label>
                            <input
                                type="date"
                                name="endDate"
                                id="endDate"
                                value={formData.endDate}
                                onChange={handleChange}
                                aria-invalid={Boolean(errors.endDate)}
                                aria-describedby={errors.endDate ? "endDate-error" : undefined}
                                className="w-full rounded-lg border border-[#9fb0aa] bg-white px-5 py-3.5 text-lg text-[#1d4237] focus:border-[#235e4f] focus:outline-none focus:ring-2 focus:ring-[#235e4f]/20"
                            />
                            {errors.endDate && (
                                <p id="endDate-error" className="text-sm text-red-700">{errors.endDate}</p>
                            )}
                        </div>
                    </div>

                    <section className="rounded-xl border border-[#d6e1d9] bg-[#f8faf7] p-4 sm:p-5">
                        <h2 className="mb-4 text-xl font-bold text-[#1d4237]">Destinationer</h2>
                        <DestinationSelect
                            key={resetKey}
                            showErrors={showErrors}
                            onCountryChange={handleCountryChange}
                            onCityChange={handleCityChange}
                            onSelectionChange={setSelectedDestination}
                        />
                        <button
                            className="mt-4 rounded-lg border border-[#235e4f] bg-white px-5 py-3 font-medium text-[#235e4f] transition hover:bg-[#235e4f] hover:text-white disabled:cursor-not-allowed disabled:opacity-40"
                            type="button"
                            onClick={addDestination}
                            disabled={!selectedDestination?.cityId}
                        >
                            Lägg till destination
                        </button>
                    </section>

                    {destinations.length > 0 && (
                        <section>
                            <h2 className="mb-3 text-xl font-bold text-[#1d4237]">Resplan</h2>
                            <ol className="flex flex-col gap-2">
                                {destinations.map((destination, index) => (
                                    <li key={destination.cityId} className="grid items-center gap-3 rounded-lg border border-[#d6e1d9] px-4 py-3 sm:grid-cols-[1fr_auto]">
                                        <div className="text-lg font-medium text-[#1d4237]">
                                            <span className="mr-2 inline-flex h-7 w-7 items-center justify-center rounded-full bg-[#e7efea] text-sm">{index + 1}</span>
                                            {destination.cityName}, {destination.countryName}
                                        </div>
                                        <div className="flex gap-2">
                                            <button
                                                className="h-10 w-10 rounded-lg border border-[#9fb0aa] text-[#1d4237] transition hover:border-[#235e4f] hover:bg-[#e7efea] disabled:cursor-not-allowed disabled:opacity-40"
                                                type="button"
                                                onClick={() => moveDestination(index, -1)}
                                                disabled={index === 0}
                                                aria-label={`Flytta ${destination.cityName} uppåt`}
                                            >
                                                🠉
                                            </button>
                                            <button
                                                className="h-10 w-10 rounded-lg border border-[#9fb0aa] text-[#1d4237] transition hover:border-[#235e4f] hover:bg-[#e7efea] disabled:cursor-not-allowed disabled:opacity-40"
                                                type="button"
                                                onClick={() => moveDestination(index, 1)}
                                                disabled={index === destinations.length - 1}
                                                aria-label={`Flytta ${destination.cityName} nedåt`}
                                            >
                                                🠋
                                            </button>
                                            <button
                                                className="rounded-lg border border-red-200 px-3 text-sm font-medium text-red-700 transition hover:bg-red-50"
                                                type="button"
                                                onClick={() => removeDestination(destination.cityId)}
                                            >
                                                Ta bort
                                            </button>
                                        </div>
                                    </li>
                                ))}
                            </ol>
                        </section>
                    )}

                    <button className="mt-2 rounded-lg bg-[#235e4f] px-6 py-3 text-lg font-medium text-white transition hover:bg-[#1d4237] disabled:cursor-not-allowed disabled:opacity-60" type="submit" disabled={submitting}>
                        {submitting ? "Skapar…" : "Skapa resa"}
                    </button>
                </fieldset>

                {submitError && <p role="alert" className="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700">{submitError}</p>}
                {successMessage && <p role="status" className="mt-4 rounded-lg border border-[#b8cbbf] bg-[#eef5f0] px-4 py-3 text-[#235e4f]">{successMessage}</p>}
            </form>
        </div>
    );
}
