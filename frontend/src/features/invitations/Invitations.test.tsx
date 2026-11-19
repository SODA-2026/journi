import "@testing-library/jest-dom/vitest";
import { cleanup, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import { afterEach, beforeEach, expect, it, vi } from "vitest";
import { IncomingInvitations } from "./IncomingInvitations";
import { InviteUserForm } from "./InviteUserForm";
import { getIncomingInvitations, inviteUser, respondToInvitation } from "./InvitationApi";
import { ApiError } from "../../api/ApiClient";

vi.mock("./InvitationApi", () => ({ getIncomingInvitations: vi.fn(), inviteUser: vi.fn(), respondToInvitation: vi.fn() }));
const invitation = { id: 1, tripId: 10, tripTitle: "Paris", inviterName: "Olena", startDate: "2026-12-01", endDate: "2026-12-05", destinations: [{ id: 1, city: "Paris", country: "Frankrike" }], status: "PENDING" as const };
beforeEach(() => vi.resetAllMocks());
afterEach(cleanup);

it("sends an invitation using the entered email", async () => {
    vi.mocked(inviteUser).mockResolvedValue(invitation);
    render(<InviteUserForm tripId={10} />);
    fireEvent.change(screen.getByRole("textbox"), { target: { value: "guest@example.com" } });
    fireEvent.click(screen.getByRole("button", { name: "Skicka inbjudan" }));
    expect(await screen.findByRole("status")).toHaveTextContent("Inbjudan har skickats");
    expect(inviteUser).toHaveBeenCalledWith(10, "guest@example.com");
});

it("shows duplicate invitation errors without reporting success", async () => {
    vi.mocked(inviteUser).mockRejectedValue(new ApiError(409, "Conflict"));
    render(<InviteUserForm tripId={10} />);
    fireEvent.change(screen.getByRole("textbox"), { target: { value: "guest@example.com" } });
    fireEvent.click(screen.getByRole("button", { name: "Skicka inbjudan" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("redan inbjuden");
    expect(screen.queryByRole("status")).not.toBeInTheDocument();
});

it("accepts an incoming invitation and links to the shared trip", async () => {
    vi.mocked(getIncomingInvitations).mockResolvedValue([invitation]);
    vi.mocked(respondToInvitation).mockResolvedValue({ ...invitation, status: "ACCEPTED" });
    render(<MemoryRouter><IncomingInvitations /></MemoryRouter>);
    const acceptButton = await screen.findByRole("button", { name: "Acceptera" });
    expect(screen.getByText("Paris, Frankrike")).toBeInTheDocument();
    expect(screen.getByText("Datum")).toBeInTheDocument();
    expect(screen.queryByRole("link")).not.toBeInTheDocument();
    fireEvent.click(acceptButton);
    expect(await screen.findByRole("link", { name: "Öppna Paris" })).toHaveAttribute("href", "/trips/10");
    expect(respondToInvitation).toHaveBeenCalledWith(1, true);
    expect(screen.queryByRole("button", { name: "Acceptera" })).not.toBeInTheDocument();
});

it("declines an invitation without adding a trip link", async () => {
    vi.mocked(getIncomingInvitations).mockResolvedValue([invitation]);
    vi.mocked(respondToInvitation).mockResolvedValue({ ...invitation, status: "DECLINED" });
    render(<MemoryRouter><IncomingInvitations /></MemoryRouter>);
    fireEvent.click(await screen.findByRole("button", { name: "Avböj" }));
    await waitFor(() => expect(screen.queryByRole("button", { name: "Avböj" })).not.toBeInTheDocument());
    expect(respondToInvitation).toHaveBeenCalledWith(1, false);
    expect(screen.queryByRole("link")).not.toBeInTheDocument();
});

it("refreshes a stale invitation and waits for a new response", async () => {
    const renewed = { ...invitation, id: 2, tripTitle: "Paris i december" };
    vi.mocked(getIncomingInvitations)
        .mockResolvedValueOnce([invitation])
        .mockResolvedValueOnce([renewed]);
    vi.mocked(respondToInvitation)
        .mockRejectedValueOnce(new ApiError(404, "Invitation not found"))
        .mockResolvedValueOnce({ ...renewed, status: "ACCEPTED" });
    render(<MemoryRouter><IncomingInvitations /></MemoryRouter>);

    fireEvent.click(await screen.findByRole("button", { name: "Acceptera" }));
    expect(await screen.findByRole("heading", { name: renewed.tripTitle })).toBeInTheDocument();
    expect(respondToInvitation).toHaveBeenCalledExactlyOnceWith(1, true);
    expect(screen.queryByRole("link")).not.toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Acceptera" }));
    expect(await screen.findByRole("link", { name: "Öppna Paris i december" })).toBeInTheDocument();
    expect(respondToInvitation).toHaveBeenLastCalledWith(2, true);
});

it("refreshes the list when another tab already answered the invitation", async () => {
    vi.mocked(getIncomingInvitations)
        .mockResolvedValueOnce([invitation])
        .mockResolvedValueOnce([]);
    vi.mocked(respondToInvitation)
        .mockRejectedValueOnce(new ApiError(409, "Invitation has already been answered"));
    render(<MemoryRouter><IncomingInvitations /></MemoryRouter>);

    fireEvent.click(await screen.findByRole("button", { name: "Acceptera" }));

    expect(await screen.findByText("Du har inga väntande inbjudningar.")).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Acceptera" })).not.toBeInTheDocument();
    expect(screen.queryByRole("alert")).not.toBeInTheDocument();
    expect(screen.queryByRole("link")).not.toBeInTheDocument();
});

it("shows a renewed invitation when the window regains focus", async () => {
    const renewed = { ...invitation, id: 2, tripTitle: "Paris i december" };
    vi.mocked(getIncomingInvitations)
        .mockResolvedValueOnce([invitation])
        .mockResolvedValueOnce([renewed]);
    vi.mocked(respondToInvitation).mockResolvedValue({ ...renewed, status: "ACCEPTED" });
    render(<MemoryRouter><IncomingInvitations /></MemoryRouter>);
    await screen.findByRole("heading", { name: invitation.tripTitle });

    fireEvent.focus(window);
    await screen.findByRole("heading", { name: renewed.tripTitle });
    expect(respondToInvitation).not.toHaveBeenCalled();

    fireEvent.click(screen.getByRole("button", { name: "Acceptera" }));
    await screen.findByRole("link", { name: "Öppna Paris i december" });
    expect(respondToInvitation).toHaveBeenCalledExactlyOnceWith(2, true);
});

it("clears the decline confirmation when the invitation list refreshes", async () => {
    const renewed = { ...invitation, id: 2, tripTitle: "Paris i december" };
    vi.mocked(getIncomingInvitations)
        .mockResolvedValueOnce([invitation])
        .mockResolvedValueOnce([renewed]);
    vi.mocked(respondToInvitation).mockResolvedValue({ ...invitation, status: "DECLINED" });
    render(<MemoryRouter><IncomingInvitations /></MemoryRouter>);

    fireEvent.click(await screen.findByRole("button", { name: "Avböj" }));
    expect(await screen.findByRole("status")).toHaveTextContent("Inbjudan avböjd.");

    fireEvent.focus(window);
    expect(await screen.findByRole("heading", { name: renewed.tripTitle })).toBeInTheDocument();
    expect(screen.queryByRole("status")).not.toBeInTheDocument();
});

it("clears the temporary trip link when the invitation list refreshes", async () => {
    vi.mocked(getIncomingInvitations)
        .mockResolvedValueOnce([invitation])
        .mockResolvedValueOnce([]);
    vi.mocked(respondToInvitation).mockResolvedValue({ ...invitation, status: "ACCEPTED" });
    render(<MemoryRouter><IncomingInvitations /></MemoryRouter>);

    fireEvent.click(await screen.findByRole("button", { name: "Acceptera" }));
    const link = await screen.findByRole("link", { name: "Öppna Paris" });

    fireEvent.focus(window);
    await waitFor(() => expect(link).not.toBeInTheDocument());
});

it.each([404, 409])("clears the temporary trip link after a stale response (%i)", async (status) => {
    const another = { ...invitation, id: 2, tripId: 11, tripTitle: "Rome" };
    vi.mocked(getIncomingInvitations).mockResolvedValueOnce([invitation, another]).mockResolvedValueOnce([]);
    vi.mocked(respondToInvitation)
        .mockResolvedValueOnce({ ...invitation, status: "ACCEPTED" })
        .mockRejectedValueOnce(new ApiError(status, "Stale invitation"));
    render(<MemoryRouter><IncomingInvitations /></MemoryRouter>);

    const [acceptParis] = await screen.findAllByRole("button", { name: "Acceptera" });
    fireEvent.click(acceptParis);
    const link = await screen.findByRole("link", { name: "Öppna Paris" });
    fireEvent.click(screen.getByRole("button", { name: "Acceptera" }));

    await screen.findByText("Du har inga väntande inbjudningar.");
    expect(link).not.toBeInTheDocument();
});
