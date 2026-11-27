import React, { useRef, useState, type FormEvent } from "react";
import type {
    ActivityFormData,
    ActivityFormErrors,
    ActivityResponse,
} from "../ActivitiesTypes";
import { validate } from "../../activities/ActivityValidation";
import { createActivity } from "../ActivitiesApi";
import { ApiError } from "../../../api/ApiClient";

interface DestinationOption {
    id: number;
    label: string;
}

interface ActivityFormProps {
    tripId: number;
    destinations: DestinationOption[];
    onCreated?: (activity: ActivityResponse) => void;
    tripStartDate?: string; 
    tripEndDate?: string;
}

const emptyForm: ActivityFormData = {
    tripDestinationId: 0,
    name: "",
    activityDate: "",
    startTime: "",
    description: "",
};

export function ActivityForm({
    tripId,
    destinations,
    onCreated,
    tripStartDate,
    tripEndDate
}: ActivityFormProps) {
    const [formData, setFormData] = useState<ActivityFormData>({
        ...emptyForm,
    });
    const [errors, setErrors] = useState<ActivityFormErrors>({});
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    // Prevents multiple requests while a request is in progress
    const requestInProgress = useRef(false);

    const clearMessages = () => {
        setSuccessMessage("");
        setSubmitError("");
    };

    const handleChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        clearMessages();

        setFormData((prev) => ({
            ...prev,
            [name]: name === "tripDestinationId" ? Number(value) : value, // ?
        }));

        setErrors((prev) => ({
            ...prev,
            [name]: undefined,
        }));
    };

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (requestInProgress.current) {
            return;
        }

        clearMessages();

        const dataToSubmit: ActivityFormData = {
            ...formData,
            name: formData.name.trim(),
        };

        const newErrors = validate(dataToSubmit, tripStartDate, tripEndDate);
        setErrors(newErrors);

        if (Object.keys(newErrors).length > 0) {
            return;
        }

        requestInProgress.current = true;
        setSubmitting(true);

        try {
            const savedActivity = await createActivity(tripId, dataToSubmit);
            setSuccessMessage(`Aktivitet "${savedActivity.name}" tillagd!`);
            setFormData({ ...emptyForm });
            setErrors({});
            onCreated?.(savedActivity);
        } catch (err) {
            console.error("Kunde inte lägga till en aktivitet: ", err);

            if(err instanceof ApiError && err.status === 400){
                setSubmitError(err.message)
            } else{
                setSubmitError("Kunde inte lägga till aktiviteten. Försök igen.")
            }
        } finally {
            requestInProgress.current = false;
            setSubmitting(false);
        }
    };

    return (
        <div className="rounded-xl border border-[#9fb0aa] bg-white px-4 py-4 sm:px-5">
            <form onSubmit={handleSubmit} noValidate className="flex flex-col gap-4">
                <fieldset disabled={submitting} className="flex flex-col gap-4">
                    <legend className="mb-1 text-lg font-bold text-[#1d4237]">
                        Lägg till aktivitet
                    </legend>

                    <div className="flex flex-col gap-1.5">
                        <label
                            htmlFor="tripDestinationId"
                            className="text-base text-[#1d4237]"
                        >
                            Destination
                        </label>
                        <select
                            name="tripDestinationId"
                            id="tripDestinationId"
                            value={formData.tripDestinationId}
                            onChange={handleChange}
                            className="w-full rounded-lg border border-[#9fb0aa] bg-white px-3 py-2 text-base text-[#1d4237] focus:border-[#235e4f] focus:outline-none focus:ring-2 focus:ring-[#235e4f]/20"
                        >
                            <option value={0}>Select your destination</option>
                            {destinations.map((d) => (
                                <option key={d.id} value={d.id}>
                                    {d.label}
                                </option>
                            ))}
                        </select>

                        {errors.tripDestinationId && (
                            <p
                                id="tripDestinationId-error"
                                className="text-sm text-red-700"
                            >
                                {errors.tripDestinationId}{" "}
                            </p>
                        )}
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <label htmlFor="name" className="text-base text-[#1d4237]">
                            Name
                        </label>
                        <input
                            type="text"
                            name="name"
                            id="name"
                            value={formData.name}
                            onChange={handleChange}
                            className="w-full rounded-lg border border-[#9fb0aa] bg-white px-3 py-2 text-base text-[#1d4237] focus:border-[#235e4f] focus:outline-none focus:ring-2 focus:ring-[#235e4f]/20"
                        />

                        {errors.name && (
                            <p id="name-error" className="text-sm text-red-700">
                                {errors.name}
                            </p>
                        )}

                    </div>

                    <div className="flex flex-col gap-1.5">
                        <label
                            htmlFor="activityDate"
                            className="text-base text-[#1d4237]"
                        >
                            Date
                        </label>
                        <input
                            type="date"
                            name="activityDate"
                            id="activityDate"
                            min={tripStartDate}
                            max={tripEndDate}
                            value={formData.activityDate}
                            onChange={handleChange}
                            className="w-full rounded-lg border border-[#9fb0aa] bg-white px-3 py-2 text-base text-[#1d4237] focus:border-[#235e4f] focus:outline-none focus:ring-2 focus:ring-[#235e4f]/20"
                        />
                        {errors.activityDate && (
                            <p id="activityDate-error" className="text-sm text-red-700">
                                {errors.activityDate}
                            </p>
                        )}
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <label
                            htmlFor="startTime"
                            className="text-base text-[#1d4237]"
                        >
                            Time (optional)
                        </label>
                        <input
                            type="time"
                            name="startTime"
                            id="startTime"
                            value={formData.startTime}
                            onChange={handleChange}
                            className="w-full rounded-lg border border-[#9fb0aa] bg-white px-3 py-2 text-base text-[#1d4237] focus:border-[#235e4f] focus:outline-none focus:ring-2 focus:ring-[#235e4f]/20"
                        />
                        {errors.startTime && (
                            <p id="startTime-error" className="text-sm text-red-700">
                                {errors.startTime}
                            </p>
                        )}
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <label
                            htmlFor="description"
                            className="text-base text-[#1d4237]"
                        >
                            Description (optional)
                        </label>
                        <textarea
                            name="description"
                            id="description"
                            rows={3}
                            value={formData.description}
                            onChange={handleChange}
                            className="w-full resize-y rounded-lg border border-[#9fb0aa] bg-white px-3 py-2 text-base text-[#1d4237] focus:border-[#235e4f] focus:outline-none focus:ring-2 focus:ring-[#235e4f]/20"
                        />
                        {errors.description && (
                            <p id="description-error" className="text-sm text-red-700">
                                {errors.description}
                            </p>
                        )}
                    </div>

                    <button
                        type="submit"
                        disabled={submitting}
                        className="w-full rounded-lg bg-[#235e4f] px-5 py-2.5 text-base text-white hover:bg-[#1d4237] disabled:cursor-not-allowed disabled:opacity-60 sm:w-fit"
                    >
                        {submitting ? "Adding..." : "Add activity"}
                    </button>
                </fieldset>

                {submitError && (
                    <p
                        role="alert"
                        className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700"
                    >
                        {submitError}
                    </p>
                )}
                {successMessage && (
                    <p
                        role="status"
                        className="rounded-lg border border-[#9fb0aa] bg-[#f4f7f3] px-4 py-3 text-[#1d4237]"
                    >
                        {successMessage}
                    </p>
                )}
            </form>
        </div>
    );
}
