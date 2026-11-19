import { useEffect, useRef, useState } from "react";
import { Link } from "react-router";
import { ApiError } from "../../api/ApiClient";
import { formatDate } from "../../shared/formatDate";
import {
    getIncomingInvitations,
    respondToInvitation,
    type Invitation,
} from "./InvitationApi";

export function IncomingInvitations() {
    const [invitations, setInvitations] = useState<Invitation[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [respondingInvitationId, setRespondingInvitationId] = useState<number | null>(null);
    const [error, setError] = useState("");
    const [acceptedInvitations, setAcceptedInvitations] = useState<Invitation[]>([]);
    const [successMessage, setSuccessMessage] = useState("");
    const refreshVersion = useRef(0);
    const responseInProgress = useRef(false);

    useEffect(() => {
        let active = true;

        async function refreshInvitations() {
            if (responseInProgress.current) return;

            const version = ++refreshVersion.current;
            const isCurrent = () => active && version === refreshVersion.current;

            try {
                const data = await getIncomingInvitations();
                if (isCurrent()) {
                    setInvitations(data);
                    setError("");
                    setSuccessMessage("");
                    setAcceptedInvitations([]);
                }
            } catch {
                if (isCurrent()) {
                    setError("Kunde inte hämta inbjudningar. Ladda om sidan för att försöka igen.");
                }
            } finally {
                if (isCurrent()) {
                    setIsLoading(false);
                }
            }
        }

        void refreshInvitations();
        window.addEventListener("focus", refreshInvitations);
        return () => {
            active = false;
            window.removeEventListener("focus", refreshInvitations);
        };
    }, []);

    async function handleRespond(invitation: Invitation, accept: boolean) {
        if (responseInProgress.current) return;
        responseInProgress.current = true;
        refreshVersion.current++;
        setRespondingInvitationId(invitation.id);
        setError("");
        setSuccessMessage("");

        try {
            const result = await respondToInvitation(invitation.id, accept);
            setInvitations((current) => current.filter((item) => item.id !== invitation.id));

            if (accept) {
                setAcceptedInvitations((current) => [...current, result]);
                setSuccessMessage("Inbjudan accepterad. Resan finns nu även under Mina resor.");
            } else {
                setSuccessMessage("Inbjudan avböjd.");
            }
        } catch (responseError) {
            if (
                responseError instanceof ApiError &&
                (responseError.status === 404 || responseError.status === 409)
            ) {
                const updatedInvitations = await getIncomingInvitations().catch(() => null);
                if (updatedInvitations !== null) {
                    setInvitations(updatedInvitations);
                    setAcceptedInvitations([]);
                    setSuccessMessage("Den tidigare inbjudan gäller inte längre. Listan har uppdaterats.");
                    return;
                }
            }
            setError("Kunde inte svara på inbjudan. Ladda om sidan och försök igen.");
        } finally {
            responseInProgress.current = false;
            setRespondingInvitationId(null);
        }
    }

    return (
        <section className="mt-6" aria-labelledby="invitations-heading">
            <h2 id="invitations-heading" className="mb-3 text-2xl font-semibold">
                Inbjudningar till dig
            </h2>
            {isLoading && <p>Laddar inbjudningar…</p>}
            {error && <p role="alert" className="text-red-700">{error}</p>}
            {successMessage && <p role="status">{successMessage}</p>}
            {!isLoading && !error && invitations.length === 0 && (
                <p>Du har inga väntande inbjudningar.</p>
            )}
            <ul className="flex flex-col gap-3">
                {invitations.map((invitation) => (
                    <li
                        key={invitation.id}
                        className="rounded-xl border border-[#9fb0aa] bg-white p-5"
                    >
                        <h3 className="text-lg font-semibold">{invitation.tripTitle}</h3>
                        <p>{invitation.inviterName} bjuder in dig till resan.</p>
                        <dl className="mt-3 grid gap-2">
                            <div>
                                <dt className="font-semibold">Datum</dt>
                                <dd>
                                    {formatDate(invitation.startDate)} – {formatDate(invitation.endDate)}
                                </dd>
                            </div>
                            <div>
                                <dt className="font-semibold">Destinationer</dt>
                                <dd>
                                    <ul>
                                        {invitation.destinations.map((destination) => (
                                            <li key={destination.id}>
                                                {destination.city}, {destination.country}
                                            </li>
                                        ))}
                                    </ul>
                                    {invitation.destinations.length === 0 && (
                                        <p>Inga destinationer tillagda än.</p>
                                    )}
                                </dd>
                            </div>
                        </dl>
                        <div className="mt-4 flex gap-3 border-t border-[#9fb0aa] pt-4">
                            <button
                                type="button"
                                disabled={respondingInvitationId !== null}
                                onClick={() => handleRespond(invitation, true)}
                                className="rounded-lg bg-[#235e4f] px-4 py-2 text-white disabled:opacity-50"
                            >
                                Acceptera
                            </button>
                            <button
                                type="button"
                                disabled={respondingInvitationId !== null}
                                onClick={() => handleRespond(invitation, false)}
                                className="rounded-lg border border-[#9fb0aa] px-4 py-2 disabled:opacity-50"
                            >
                                Avböj
                            </button>
                        </div>
                    </li>
                ))}
            </ul>
            {acceptedInvitations.map((invitation) => (
                <p key={invitation.id} className="mt-3">
                    <Link className="underline" to={`/trips/${invitation.tripId}`}>
                        Öppna {invitation.tripTitle}
                    </Link>
                </p>
            ))}
        </section>
    );
}
