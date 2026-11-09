import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { api, ApiError, clearCsrf } from "./ApiClient";

const fetchMock = vi.fn();
beforeEach(() => { clearCsrf(); vi.stubGlobal("fetch", fetchMock); });
afterEach(() => { vi.unstubAllGlobals(); vi.restoreAllMocks(); fetchMock.mockReset(); });
describe("session API client", () => {
    it("sends credentials and fetches CSRF before a mutation", async () => {
        fetchMock.mockResolvedValueOnce(new Response(JSON.stringify({ headerName: "X-CSRF-TOKEN", token: "token" })))
            .mockResolvedValueOnce(new Response(JSON.stringify({ id: 42 })));
        await expect(api("/trips/create", { method: "POST", body: "{}" })).resolves.toEqual({ id: 42 });
        expect(fetchMock.mock.calls[0][0]).toBe("http://localhost:8080/api/auth/csrf");
        const options = fetchMock.mock.calls[1][1];
        expect(options.credentials).toBe("include");
        expect(options.headers.get("X-CSRF-TOKEN")).toBe("token");
    });
    it("handles logout without attempting to parse a 204 body", async () => {
        fetchMock.mockResolvedValueOnce(new Response(JSON.stringify({ headerName: "X-CSRF-TOKEN", token: "token" })))
            .mockResolvedValueOnce(new Response(null, { status: 204 }));
        await expect(api("/api/auth/logout", { method: "POST" })).resolves.toBeUndefined();
    });
    it("announces an expired session and preserves the HTTP error", async () => {
        const listener = vi.fn();
        window.addEventListener("session-expired", listener);
        fetchMock.mockResolvedValueOnce(new Response('{"message":"Authentication required"}', { status: 401 }));
        await expect(api("/api/auth/me")).rejects.toMatchObject({ status: 401, message: "Authentication required" });
        expect(listener).toHaveBeenCalledOnce();
        window.removeEventListener("session-expired", listener);
    });
    it("does not retry a rejected mutation", async () => {
        fetchMock.mockResolvedValueOnce(new Response(JSON.stringify({ headerName: "X-CSRF-TOKEN", token: "token" })))
            .mockResolvedValueOnce(new Response(null, { status: 403 }));
        await expect(api("/trips/create", { method: "POST", body: "{}" })).rejects.toBeInstanceOf(ApiError);
        expect(fetchMock).toHaveBeenCalledTimes(2);
    });
});
