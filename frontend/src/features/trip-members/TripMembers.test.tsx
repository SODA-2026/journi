import "@testing-library/jest-dom/vitest";
import { cleanup, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { afterEach, beforeEach, expect, it, vi } from "vitest";
import { TripMembers } from "./TripMembers";
import { getTripMembers, removeTripMember } from "./TripMemberApi";

vi.mock("./TripMemberApi", () => ({ getTripMembers: vi.fn(), removeTripMember: vi.fn() }));
const member = { tripId: 10, userId: 2, name: "Sam" };
beforeEach(() => {
    vi.mocked(getTripMembers).mockResolvedValue([member]);
    vi.spyOn(window, "confirm").mockReturnValue(true);
});
afterEach(() => { cleanup(); vi.resetAllMocks(); vi.restoreAllMocks(); });

it("confirms deletion and removes only the selected participant after success", async () => {
    vi.mocked(getTripMembers).mockResolvedValue([member, { ...member, userId: 3, name: "Anders" }]);
    vi.mocked(removeTripMember).mockResolvedValue(undefined);
    render(<TripMembers tripId={10} />);
    fireEvent.click(await screen.findByRole("button", { name: "Ta bort Sam" }));
    expect(window.confirm).toHaveBeenCalledWith(expect.stringContaining("Sam"));
    expect(await screen.findByRole("status")).toHaveTextContent("Sam har tagits bort");
    expect(removeTripMember).toHaveBeenCalledWith(10, 2);
    expect(screen.queryByRole("button", { name: "Ta bort Sam" })).not.toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Ta bort Anders" })).toBeInTheDocument();
});

it("keeps the membership when confirmation is cancelled", async () => {
    vi.mocked(window.confirm).mockReturnValue(false);
    render(<TripMembers tripId={10} />);
    fireEvent.click(await screen.findByRole("button", { name: "Ta bort Sam" }));
    expect(removeTripMember).not.toHaveBeenCalled();
    expect(screen.getByRole("button", { name: "Ta bort Sam" })).toBeInTheDocument();
});

it("keeps the member visible and shows an error when deletion fails", async () => {
    vi.mocked(removeTripMember).mockRejectedValue(new Error("Network error"));
    render(<TripMembers tripId={10} />);
    fireEvent.click(await screen.findByRole("button", { name: "Ta bort Sam" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("Kunde inte ta bort deltagaren");
    expect(screen.getByRole("button", { name: "Ta bort Sam" })).toBeEnabled();
});

it("disables removal while the request is pending", async () => {
    let finish!: () => void;
    vi.mocked(removeTripMember).mockReturnValue(new Promise<void>(resolve => { finish = resolve; }));
    render(<TripMembers tripId={10} />);
    fireEvent.click(await screen.findByRole("button", { name: "Ta bort Sam" }));
    expect(screen.getByRole("button", { name: "Ta bort Sam" })).toBeDisabled();
    finish();
    await waitFor(() => expect(screen.getByRole("status")).toBeInTheDocument());
});
