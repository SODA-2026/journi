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

    // TODO: Validation for country & city requirement
    if (values.countryId === null) {
        errors.countryId = "Country is required";
    }
    if (values.cityId === null) {
        errors.cityId = "City is required";
    }

    return errors;
}
