import type { ActivityFormData, ActivityFormErrors, ActivityResponse } from "./ActivitiesTypes";


export function validate(data: ActivityFormData): ActivityFormErrors {
    const errors: ActivityFormErrors = {};

    if (!data.tripDestinationId)
        errors.tripDestinationId = "Välj en destination";
    if (!data.name.trim()) errors.name = "Namn är obligatorisk";
    else if (data.name.length > 255)
        errors.name = "Max 255 karaktärer på namn.";
    if (!data.activityDate) errors.activityDate = "Datum är obligatorisk";
    if(data.activityDate && tripStartDate && tripEndDate && (
        data.activityDate < tripStartDate || data.activityDate > tripEndDate
    )) {
        errors.activityDate = `Datumet måste ligga mellan ${tripStarDate} och ${tripEndDate}`
    }
    if (data.startTime && !/^([01]\d|2[0-3]):[0-5]\d$/.test(data.startTime))
        errors.startTime = "Ogiltig tid (HH:MM)";
    if (data.description.length > 2000)
        errors.description = "Max 2000 karaktärer på beskrivning.";

    return errors;
}
