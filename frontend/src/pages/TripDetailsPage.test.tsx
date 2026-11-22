import "@testing-library/jest-dom/vitest";
import { cleanup, fireEvent, render, screen } from "@testing-library/react";
import { Link, MemoryRouter, Route, Routes } from "react-router";
import { afterEach, beforeEach, expect, it, vi } from "vitest";
import { ApiError } from "../api/ApiClient";
import { getSentInvitations, inviteUser } from "../features/invitations/InvitationApi";
import { getTripInfo, getTripDepartures } from "../features/trips/TripFetch";
import { TripDetailsPage } from "./TripDetailsPage";

vi.mock("../features/invitations/InvitationApi", () => ({
    getSentInvitations: vi.fn(),
    inviteUser: vi.fn(),
}));
vi.mock("../features/trips/TripFetch", () => ({ getTripInfo: vi.fn(), getTripDepartures: vi.fn() }));
const auth = vi.hoisted(() => ({ user: { id: 1 } }));
vi.mock("../context/AuthContext", () => ({ useAuth: () => auth }));

const trip = {
    id: 10,
    title: "First trip",
    ownerId: 1,
    ownerName: "Trip owner",
    destinations: [],
    startDate: "2026-12-01",
    endDate: "2026-12-05",
    createdAt: "2026-11-01",
};

beforeEach(() => {
    vi.mocked(getTripDepartures).mockResolvedValue([]);
    auth.user.id = 1;
    vi.mocked(getSentInvitations).mockResolvedValue([]);
});

afterEach(() => {
    cleanup();
    vi.resetAllMocks();
    vi.restoreAllMocks();
});

function renderTripRoute() {
    render(
        <MemoryRouter initialEntries={["/trips/10"]}>
            <Link to="/trips/20">Next trip</Link>
            <Routes>
                <Route path="/trips/:id" element={<TripDetailsPage />} />
            </Routes>
        </MemoryRouter>,
    );
}

it("hides the previous trip and invitation form while the next trip loads", async () => {
    vi.mocked(getTripInfo).mockResolvedValueOnce(trip).mockReturnValueOnce(new Promise(() => {}));
    renderTripRoute();
    await screen.findByRole("heading", { name: trip.title });
    expect(screen.getByRole("button", { name: "Skicka inbjudan" })).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Lämna resan" })).not.toBeInTheDocument();

    fireEvent.click(screen.getByRole("link", { name: "Next trip" }));

    expect(screen.getByText("Laddar resa...")).toBeInTheDocument();
    expect(screen.queryByRole("heading", { name: trip.title })).not.toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Skicka inbjudan" })).not.toBeInTheDocument();
});

it("shows the leave button to an invited participant", async () => {
    auth.user.id = 2;
    vi.mocked(getTripInfo).mockResolvedValue(trip);
    renderTripRoute();
    expect(await screen.findByRole("button", { name: "Lämna resan" })).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Skicka inbjudan" })).not.toBeInTheDocument();
});

it("shows departures to remaining participants and refreshes on returning to the tab", async () => {
    auth.user.id = 3;
    vi.mocked(getTripInfo).mockResolvedValue(trip);
    vi.mocked(getTripDepartures)
        .mockResolvedValueOnce([])
        .mockResolvedValueOnce([{ id: 1, memberName: "Olena", createdAt: "2026-11-22T20:00:00Z" }]);
    renderTripRoute();
    expect(await screen.findByText("Ingen har lämnat resan ännu.")).toBeInTheDocument();
    fireEvent(window, new Event("focus"));
    expect(await screen.findByText("Olena har lämnat resan.")).toBeInTheDocument();
    expect(getTripDepartures).toHaveBeenLastCalledWith(10);
});

it("clears the previous trip error on navigation", async () => {
    vi.spyOn(console, "error").mockImplementationOnce(() => {});
    vi.mocked(getTripInfo)
        .mockRejectedValueOnce(new ApiError(403, "Forbidden"))
        .mockResolvedValueOnce({ ...trip, id: 20, title: "Second trip" });
    renderTripRoute();
    await screen.findByText("Du har inte tillgång till resan");

    fireEvent.click(screen.getByRole("link", { name: "Next trip" }));

    expect(await screen.findByRole("heading", { name: "Second trip" })).toBeInTheDocument();
    expect(screen.queryByText("Du har inte tillgång till resan")).not.toBeInTheDocument();
});

it("shows only this trip's invitees and refreshes the list after sending", async () => {
    const anotherTrip = {
        id: 1,
        tripId: 20,
        tripTitle: "Another trip",
        inviteeEmail: "other@example.com",
        status: "PENDING" as const,
    };
    const sent = {
        id: 2,
        tripId: trip.id,
        tripTitle: trip.title,
        inviteeEmail: "guest@example.com",
        status: "PENDING" as const,
    };
    vi.mocked(getTripInfo).mockResolvedValue(trip);
    vi.mocked(getSentInvitations)
        .mockResolvedValueOnce([anotherTrip])
        .mockResolvedValueOnce([sent, anotherTrip]);
    vi.mocked(inviteUser).mockResolvedValue({
        ...sent,
        inviterName: "Trip owner",
        destinations: [],
        startDate: trip.startDate,
        endDate: trip.endDate,
    });

    renderTripRoute();
    expect(await screen.findByText("Inga inbjudningar till den här resan än.")).toBeInTheDocument();
    fireEvent.change(screen.getByRole("textbox", { name: /E-postadress/ }), {
        target: { value: sent.inviteeEmail },
    });
    fireEvent.click(screen.getByRole("button", { name: "Skicka inbjudan" }));

    expect(await screen.findByText("Till guest@example.com")).toBeInTheDocument();
    expect(screen.getByText("Väntar på svar")).toBeInTheDocument();
    expect(screen.queryByText("Till other@example.com")).not.toBeInTheDocument();
});
