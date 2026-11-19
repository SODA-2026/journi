import { useState, type FormEvent } from "react";
import { ApiError } from "../../api/ApiClient";
import { inviteUser } from "./InvitationApi";

type InviteUserFormProps = {
    tripId: number;
    onSent?: () => void;
};

export function InviteUserForm({ tripId, onSent }: InviteUserFormProps) {
    const [email, setEmail] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    function handleEmailChange(event: React.ChangeEvent<HTMLInputElement>) {
        setEmail(event.target.value);
        setSuccessMessage("");
    }

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setSubmitting(true);
        setSubmitError("");
        setSuccessMessage("");

        try {
            await inviteUser(tripId, email.trim());
            setEmail("");
            setSuccessMessage("Inbjudan har skickats.");
            onSent?.();
        } catch (err) {
            const status = err instanceof ApiError ? err.status : 0;

            if (status === 404) {
                setSubmitError("Ingen registrerad användare med den e-postadressen hittades.");
            } else if (status === 409) {
                setSubmitError("Användaren är redan inbjuden eller deltar redan i resan.");
            } else if (status === 400) {
                setSubmitError("Kontrollera e-postadressen. Du kan inte bjuda in dig själv.");
            } else if (status === 403) {
                setSubmitError("Endast resans skapare kan bjuda in användare.");
            } else {
                setSubmitError("Kunde inte skicka inbjudan. Försök igen.");
            }
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <section className="my-6 rounded-xl border border-[#9fb0aa] bg-white p-5">
            <h3 className="mb-3 text-xl font-semibold">Bjud in till resan</h3>
            <form onSubmit={handleSubmit} className="flex flex-wrap items-end gap-3">
                <label className="flex flex-col gap-1">
                    E-postadress till en registrerad användare
                    <input
                        type="email"
                        required
                        value={email}
                        disabled={submitting}
                        onChange={handleEmailChange}
                        className="rounded-lg border border-[#9fb0aa] px-3 py-2"
                    />
                </label>
                <button
                    type="submit"
                    disabled={submitting}
                    className="rounded-lg bg-[#235e4f] px-4 py-2 text-white disabled:opacity-50"
                >
                    {submitting ? "Skickar…" : "Skicka inbjudan"}
                </button>
            </form>
            {successMessage && (
                <p role="status" className="mt-3">{successMessage}</p>
            )}
            {submitError && (
                <p role="alert" className="mt-3 text-red-700">{submitError}</p>
            )}
        </section>
    );
}
