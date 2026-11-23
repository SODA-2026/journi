export class ApiError extends Error {
    status: number;
    constructor(status: number, message: string) { super(message); this.status = status; }
}
const base = "http://localhost:8080";
let csrf: { headerName: string; token: string } | undefined;
export function clearCsrf() { csrf = undefined; }
export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
    const headers = new Headers(options.headers);
    if (options.body) headers.set("Content-Type", "application/json");
    if (!["GET", "HEAD", "OPTIONS"].includes(options.method ?? "GET")) {
        csrf ??= await api("/api/auth/csrf");
        headers.set(csrf!.headerName, csrf!.token);
    }
    const response = await fetch(base + path, { ...options, headers, credentials: "include" });
    if (!response.ok) {
        const body = await response.json().catch(() => ({}));
        if (response.status === 401) window.dispatchEvent(new Event("session-expired"));
        throw new ApiError(response.status, body.message ?? body.detail ?? `Request failed (${response.status})`);
    }
    return response.status === 204 ? undefined as T : response.json();
}
