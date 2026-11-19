import { useEffect, useState, type FormEvent } from "react";
import type { TripUpdateRequest } from "../trips";
import { Link, useNavigate, useParams } from "react-router";
import { getTripInfo, updateTripFetch } from "../TripFetch";
import { ApiError } from "../../../api/ApiClient";

const emptyUpdateForm: TripUpdateRequest = {
    title: "",
    startDate: "",
    endDate: "",
};
export function UpdateTrip() {
    const [updateForm, setUpdateForm] = useState({ ...emptyUpdateForm });
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [submitError, setSubmitError] = useState("");
    const navigate = useNavigate();

    const params = useParams();
    const tripId = Number(params.id);

    useEffect(() => {
        getTripInfo(tripId)
            .then((data) =>
                setUpdateForm({
                    title: data.title,
                    startDate: data.startDate,
                    endDate: data.endDate,
                })
            )
            .catch((err) => {
                console.error(err);
                if (err instanceof ApiError && err.status === 404) {
                    setError("Resan hittades inte");
                } else if (err instanceof ApiError && err.status === 403) {
                    setError("Du har inte tillgång till resan");
                } else setError("Kunde inte hämta resan, försök igen");
            })
            .finally(() => setIsLoading(false));
    }, [tripId]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setUpdateForm({ ...updateForm, [name]: value });
    };

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        setSubmitError("");

        if (updateForm.title.trim() === "") {
            setSubmitError("Titeln får inte vara tom");
            return;
        }

        try {
            await updateTripFetch(tripId, updateForm);
            navigate(`/trips/${tripId}`);
        } catch (err) {
            console.error(err);
            if (err instanceof ApiError && err.status === 400) {
                setSubmitError(err.message);
            } else {
                setSubmitError("Kunde inte spara ändringarna, försök igen");
            }
        }
    };

    if (isLoading) {
        return (
            <div className="rounded-xl border border-[#d6e1d9] bg-white px-6 py-8 text-[#526c62]">
                Laddar resa...
            </div>
        );
    }

    if (error)
        return (
            <div className="mx-auto max-w-3xl">
                <Link
                    to="/trips"
                    className="mb-5 inline-flex items-center text-sm font-medium text-[#235e4f] hover:text-[#1d4237] hover:underline"
                >
                    ← Tillbaka till mina resor
                </Link>
                <div
                    role="alert"
                    className="rounded-xl border border-red-200 bg-red-50 px-6 py-5 text-red-700"
                >
                    {error}
                </div>
            </div>
        );
    return (
        <div>
            <Link to={`/trips/${tripId}`}>Avbryt</Link>
            <h2>Redigera resa</h2>
            <form onSubmit={handleSubmit}>
                <label htmlFor="title">Titel</label>
                <input
                    id="title"
                    type="text"
                    name="title"
                    value={updateForm.title}
                    onChange={handleChange}
                    required
                    maxLength={150}
                />
                <label htmlFor="startDate">Startdatum</label>
                <input
                    type="date"
                    name="startDate"
                    id="startDate"
                    value={updateForm.startDate}
                    onChange={handleChange}
                    required
                />
                <label htmlFor="endDate">Slutdatum</label>
                <input
                    type="date"
                    name="endDate"
                    id="endDate"
                    value={updateForm.endDate}
                    onChange={handleChange}
                    required
                />
                {submitError && <p>{submitError}</p>}
                <button type="submit">Spara</button>
            </form>
        </div>
    );
}
