import type { ActivityFormData, ActivityFormErrors } from "./ActivitiesTypes";

export function validate(data: ActivityFormData): ActivityFormErrors {
    const errors: ActivityFormErrors = {};

    if (!data.tripDestinationId)
        errors.tripDestinationId = "Select a destination";
    if (!data.name.trim()) errors.name = "Name is required";
    else if (data.name.length > 255)
        errors.name = "Name must be at most 255 characters";
    if (!data.activityDate) errors.activityDate = "Date is required";
    if (data.startTime && !/^([01]\d|2[0-3]):[0-5]\d$/.test(data.startTime))
        errors.startTime = "Invalid time (HH:MM)";
    if (data.description.length > 2000)
        errors.description = "Description must be at most 2000 characters";

    return errors;
}
