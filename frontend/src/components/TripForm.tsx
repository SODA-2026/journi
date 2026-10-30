import { useState } from "react";
import {
    validate,
    type TripFormData,
    type TripFormErrors,
} from "../utils/TripValidation";
import { createTrip } from "../api/TripFetch.ts";

const emptyForm: TripFormData = { title: "", startDate: "", endDate: "" };

export function TripForm() {
    const [formData, setFormData] = useState<TripFormData>(emptyForm);

    const [errors, setErrors] = useState<TripFormErrors>({});
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;

        setSuccessMessage("");
        setSubmitError("");

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));

        setErrors((prev) => ({
            ...prev,
            [name]: undefined,
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Clears old messages
        setSubmitError("");
        setSuccessMessage("");

        const newErrors = validate(formData);
        setErrors(newErrors);

        if (Object.keys(newErrors).length > 0) {
            return;
        }

        setSubmitting(true);
        try {
            const savedTrip = await createTrip(formData);
            setSuccessMessage(`Trip "${savedTrip.title}" created!`);
            setFormData(emptyForm);
        } catch (err) {
            console.error("Failed to create trip:", err);
            setSubmitError("Could not create the trip. Please try again.");
        } finally {
            setSubmitting(false);
        }

        // console.log(formData);
    };

    return (
        <div>
            <form onSubmit={handleSubmit}>
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

                <div>Country/city component goes here</div>

                {submitError && <p role="alert">{submitError}</p>}
                {successMessage && <p role="status">{successMessage}</p>}
                
                {/* disabled prevents double submits */}
                <button type="submit" disabled={submitting}>
                    Create trip
                </button>
            </form>
        </div>
    );
}
