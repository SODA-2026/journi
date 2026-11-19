import { useEffect, useState } from "react";
import { Link } from "react-router";
import { useAuth } from "../context/AuthContext";
import { getIncomingInvitations } from "../features/invitations/InvitationApi";
import { PageHeader } from "../shared/PageHeader";

export function MyDashboard() {
    const { user } = useAuth();
    const [pendingCount, setPendingCount] = useState<number | null>(null);

    useEffect(() => {
        let latestRequest = 0;

        async function updatePendingCount() {
            const request = ++latestRequest;
            const count = await getIncomingInvitations()
                .then((invitations) => invitations.length)
                .catch(() => null);
            if (request === latestRequest) setPendingCount(count);
        }

        void updatePendingCount();
        window.addEventListener("focus", updatePendingCount);
        return () => {
            latestRequest++;
            window.removeEventListener("focus", updatePendingCount);
        };
    }, []);

    const invitationLabel = pendingCount === null
        ? "Visa inbjudningar"
        : pendingCount === 1
            ? "1 väntande inbjudan"
            : `${pendingCount} väntande inbjudningar`;

    return (
        <>
            <PageHeader
                title="Min översikt"
                description={`Välkommen, ${user?.name ?? "resenär"}! Här börjar din nästa resa.`}
            />
            <Link
                to="/invitations"
                className="mt-6 inline-flex rounded-lg bg-[#235e4f] px-4 py-2 font-medium text-white hover:bg-[#1d4237]"
            >
                {invitationLabel}
            </Link>
        </>
    );
}
