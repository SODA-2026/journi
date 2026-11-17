export function formatDate(isoString: string): string {
    return new Date(isoString).toLocaleDateString("sv-SE", {
        day: "numeric",
        month: "short",
        year: "numeric",
    });
}
