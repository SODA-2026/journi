import { useState } from "react"
import { validate, type TripFormData, type TripFormErrors } from '../utils/TripValidation';



export function TripForm() {
    const [formData, setFormData] = useState<TripFormData>({
        title: '',
        startDate: '',
        endDate: '',
    })

    const [errors, setErrors] = useState<TripFormErrors>({})
    const [submitting, setSumbitting] = useState(false)

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;

        setFormData(prev => ({
            ...prev,
            [name]: value,
        }))

        setErrors(prev => ({
            ...prev,
            [name]: undefined
        }))
    }

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        const newErrors = validate(formData)
        setErrors(newErrors)

        if(Object.keys(newErrors).length > 0) {
            return
        }
        console.log(formData)
    }

    return(
        <div>
        <form onSubmit={handleSubmit} >
            <div>
                <label htmlFor="title">Title</label>
                <input type="text" name="title" id="title" 
                value={formData.title} onChange={handleChange} />
                {errors.title && <p id="title-error">{errors.title}</p>}
            </div>

            <div>
                <label htmlFor="startDate">Start Date</label>
                <input type="date" name="startDate" id="startDate" 
                value={formData.startDate} onChange={handleChange} />
                {errors.startDate && <p id="startDate-error">{errors.startDate} </p>}
            </div>
            
            
            <div>
                 <label htmlFor="endDate">End Date</label>
                <input type="date" name="endDate" id="endDate" 
                value={formData.endDate} onChange={handleChange}/>
                {errors.endDate && <p id="endDate-error">{errors.endDate} </p>}
            </div>

            <div>Country/city component goes here</div>

            <button type="submit">Create trip</button>
        </form>
        
        </div>
    )
}