import { useCallback, useEffect, useRef, useState } from "react";
import { Link } from "react-router";
import { InviteUserForm } from "./InviteUserForm";
import { getSentInvitations, type SentInvitation } from "./InvitationApi";

const statusLabels: Record<SentInvitation["status"], string> = {
    PENDING: "Väntar på svar",
    ACCEPTED: "Accepterad",
    DECLINED: "Avböjd",
};

type SentInvitationsProps = {
    tripId?: number;
};

export function SentInvitations({ tripId }: SentInvitationsProps) {
    const [invitations, setInvitations] = useState<SentInvitation[] | null>(null);
    const [hasError, setHasError] = useState(false);

    const currentRequest = useRef(0);

    const refresh = useCallback(async () => {
        const request = ++currentRequest.current;
        try {
            const items = await getSentInvitations();
            if (request === currentRequest.current) {
                setInvitations(items);
                setHasError(false);
            }
        } catch {
            if (request === currentRequest.current) setHasError(true);
        }
    }, []);

    useEffect(() => {
        void refresh();
        window.addEventListener("focus", refresh);
        return () => {
            currentRequest.current++;
            window.removeEventListener("focus", refresh);
        };
    }, [refresh]);

    const visibleInvitations = tripId === undefined
        ? invitations
        : invitations?.filter((invitation) => invitation.tripId === tripId);

    return (
        <>
            {tripId !== undefined && <InviteUserForm tripId={tripId} onSent={refresh} />}
            <section className="mt-6" aria-labelledby="sent-invitations-heading">
                <h2 id="sent-invitations-heading" className="mb-3 text-2xl font-semibold">
                    {tripId === undefined ? "Inbjudningar du har skickat" : "Inbjudna till resan"}
                </h2>
                {hasError && <p role="alert" className="text-red-700">Kunde inte hämta skickade inbjudningar.</p>}
                {!hasError && invitations === null && <p>Laddar skickade inbjudningar…</p>}
                {!hasError && visibleInvitations?.length === 0 && (
                    <p>{tripId === undefined ? "Du har inte skickat några inbjudningar." : "Inga inbjudningar till den här resan än."}</p>
                )}
                {!!visibleInvitations?.length && (
                    <ul className="flex flex-col gap-3">
                        {visibleInvitations.map((invitation) => (
                            <li key={invitation.id} className="rounded-xl border border-[#9fb0aa] bg-white p-5">
                                {tripId === undefined && (
                                    <Link to={`/trips/${invitation.tripId}`} className="font-semibold underline">
                                        {invitation.tripTitle}
                                    </Link>
                                )}
                                <p>Till {invitation.inviteeEmail}</p>
                                <p>{statusLabels[invitation.status]}</p>
                            </li>
                        ))}
                    </ul>
                )}
            </section>
        </>
    );
}
