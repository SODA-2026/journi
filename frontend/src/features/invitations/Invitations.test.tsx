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
