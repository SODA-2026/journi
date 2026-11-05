import { useRef, useState, type FormEvent } from "react";
import { createTrip } from "./TripFetch.ts";
import { validate } from "./TripValidation";
import type { TripFormData, TripFormErrors } from "./trips.ts";
import { DestinationSelect } from "../destinations/DestinationSelect.tsx";

const emptyForm: TripFormData = {
    title: "",
    startDate: "",
    endDate: "",
    countryId: 0,
    cityId: 0,
};

export function TripForm() {
    const [formData, setFormData] = useState<TripFormData>({ ...emptyForm });
    const [errors, setErrors] = useState<TripFormErrors>({});
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    const [showErrors, setShowErrors] = useState(false);
    const [resetKey, setResetKey] = useState(0);

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
        };

        const newErrors = validate(dataToSubmit);

        if (Object.keys(newErrors).length > 0) {
            return;
        }

        requestInProgress.current = true;
        setSubmitting(true);

        try {
            const savedTrip = await createTrip(dataToSubmit);
            setSuccessMessage(`Trip "${savedTrip.title}" created!`);
            setFormData({ ...emptyForm });
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

    return (
        <div>
            <form onSubmit={handleSubmit} noValidate>
                <fieldset disabled={submitting}>
                    <div>
                        <label htmlFor="title">Title</label>
                        <input
                            type="text"
                            name="title"
                            id="title"
                            value={formData.title}
                            onChange={handleChange}
                        />
                        {errors.title && <p id="title-error">{errors.title}</p>}
                    </div>

                    <div>
                        <label htmlFor="startDate">Start Date</label>
                        <input
                            type="date"
                            name="startDate"
                            id="startDate"
                            value={formData.startDate}
                            onChange={handleChange}
                        />
                        {errors.startDate && (
                            <p id="startDate-error">{errors.startDate} </p>
                        )}
                    </div>

                    <div>
                        <label htmlFor="endDate">End Date</label>
                        <input
                            type="date"
                            name="endDate"
                            id="endDate"
                            value={formData.endDate}
                            onChange={handleChange}
                        />
                        {errors.endDate && (
                            <p id="endDate-error">{errors.endDate} </p>
                        )}
                    </div>

                    <div>
                        <DestinationSelect
                            key={resetKey}
                            showErrors={showErrors}
                            onCountryChange={handleCountryChange}
                            onCityChange={handleCityChange}
                        />
                    </div>

                    <button type="submit" disabled={submitting}>
                        {submitting ? "Creating…" : "Create trip"}
                    </button>
                </fieldset>

                {submitError && <p role="alert">{submitError}</p>}

                {successMessage && <p role="status">{successMessage}</p>}

                {/*                 
{/* 
                {submitError && <p role="alert">{submitError}</p>}
                {successMessage && <p role="status">{successMessage}</p>}

                {/* disabled prevents double submits */}
                {/* <button type="submit" disabled={submitting}>
                    Create trip
                </button> */}
            </form>
        </div>
    );
}
