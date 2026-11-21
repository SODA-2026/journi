import { IncomingInvitations } from "../features/invitations/IncomingInvitations";
import { SentInvitations } from "../features/invitations/SentInvitations";
import { PageHeader } from "../shared/PageHeader";

export function InvitationsPage() {
    return (
        <>
            <PageHeader title="Inbjudningar" />
            <IncomingInvitations />
            <SentInvitations />
        </>
    );
}
