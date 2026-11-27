import type {
    ActivityFormData,
    ActivityFormErrors,
} from "./ActivitiesTypes";

export interface EditableActivityFields {
    name: string;
    activityDate: string;
    startTime: string;
    description: string;
}

function formatTripDate(iso: string): string {
    return new Intl.DateTimeFormat("sv-SE", {
        day: "numeric",
        month: "long",
        year: "numeric",
    }).format(new Date(`${iso}T00:00:00`)); // T00:00:00 avoids timezone shifts
}

export function validateEditableFields(
    data: EditableActivityFields,
    tripStartDate?: string,
    tripEndDate?: string,
): ActivityFormErrors {
    const errors: ActivityFormErrors = {};

    if (!data.name.trim()) errors.name = "Namn är obligatorisk";
    else if (data.name.length > 255)
        errors.name = "Max 255 karaktärer på namn.";

    if (!data.activityDate) errors.activityDate = "Välj ett datum för aktiviteten.";
    else if (
        data.activityDate &&
        tripStartDate &&
        tripEndDate &&
        (data.activityDate < tripStartDate || data.activityDate > tripEndDate)
    ) {
        errors.activityDate =
            `Datumet ligger utanför resan. Välj ett datum mellan ` +
            `${formatTripDate(tripStartDate)} och ${formatTripDate(tripEndDate)}.`;
    }

    if (data.startTime && !/^([01]\d|2[0-3]):[0-5]\d$/.test(data.startTime))
        errors.startTime = "Ogiltig tid (HH:MM)";
    if (data.description.length > 2000)
        errors.description = "Max 2000 karaktärer på beskrivning.";

    return errors;
}

export function validate(
    data: ActivityFormData,
    tripStartDate?: string,
    tripEndDate?: string,
): ActivityFormErrors {
    const errors = validateEditableFields(data, tripStartDate, tripEndDate);

    if (!data.tripDestinationId)
        errors.tripDestinationId = "Välj en destination";

    return errors;
}
