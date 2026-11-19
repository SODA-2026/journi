// Trip form types and validation
import type { TripFormData, TripFormErrors } from "./trips";

export function validate(values: TripFormData): TripFormErrors {
    const errors: TripFormErrors = {};

    if (values.title.trim() === "") {
        errors.title = "Title is required";
    }

    if (values.startDate === "") {
        errors.startDate = "Start date must be filled!";
    }

    if (values.endDate === "") {
        errors.endDate = "End date must be filled!";
    } else if (values.startDate !== "" && values.endDate < values.startDate) {
        errors.endDate = "The end date cannot be before start date!";
    }

    const hasAddedDestinations =
        Array.isArray(values.destinations) && values.destinations.length > 0;

    if (!hasAddedDestinations && (!values.countryId || values.countryId === 0)) {
        errors.countryId = "Country is required";
    }
    if (!hasAddedDestinations && (!values.cityId || values.cityId === 0)) {
        errors.cityId = "City is required";
    }

    return errors;
}
