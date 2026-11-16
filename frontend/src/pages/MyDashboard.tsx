import { useAuth } from "../context/AuthContext";
import { PageHeader } from "../shared/PageHeader";
import { IncomingInvitations } from "../features/invitations/IncomingInvitations";

export function MyDashboard() {
    const { user } = useAuth();

    return (
        <>
            <PageHeader
                title="Min översikt"
                description={`Välkommen, ${user?.name ?? "resenär"}! Här börjar din nästa resa.`}
            />
            <IncomingInvitations />
        </>
    );
}
