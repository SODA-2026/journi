import { useAuth } from "../context/AuthContext";
import { PageHeader } from "../shared/PageHeader";

export function MyDashboard() {
    const { user } = useAuth();

    return (
        <PageHeader
            title="Min översikt"
            description={`Välkommen, ${user?.name ?? "resenär"}! Här börjar din nästa resa.`}
        />
    );
}
