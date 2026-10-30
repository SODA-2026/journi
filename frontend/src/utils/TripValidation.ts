// Trip form types and validation

export interface TripFormData {
    title: string,
    startDate: string,
    endDate: string,
}

export interface TripFormErrors {
    title?: string,
    startDate?: string,
    endDate?: string,
}

export function validate(values: TripFormData): TripFormErrors{
    const errors: TripFormErrors = {}

    if(values.title.trim() === ''){
        errors.title = 'Title is required'
    }

    if(values.startDate === '') {
        errors.startDate = 'Start date must be filled!'
    }

    if(values.endDate === '') {
        errors.endDate = 'End date must be filled!'
    } else if(values.startDate !== '' && values.endDate < values.startDate ) {
        errors.endDate = 'The end date cannot be before start date!'
    }

    return errors;
}