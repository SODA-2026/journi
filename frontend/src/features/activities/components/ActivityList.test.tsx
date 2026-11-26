import "@testing-library/jest-dom/vitest";
import { cleanup, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { afterEach, expect, it, vi } from "vitest";
import type { ActivityResponse } from "../ActivitiesTypes";
import { ActivityList } from "./ActivityList";

const destinations = [{ id: 11, label: "Malmö" }];

const activity: ActivityResponse = {
    id: 100,
    tripDestinationId: 11,
    name: "Fika",
    activityDate: "2026-07-03",
    startTime: "10:00:00",
    description: "Kaffe",
    status: "PLANNED",
};

function renderList(onUpdate = vi.fn(), onDelete = vi.fn()) {
    render(
        <ActivityList
            activities={[activity]}
            destinations={destinations}
            onUpdate={onUpdate}
            onDelete={onDelete}
        />,
    );
    return { onUpdate, onDelete };
}

afterEach(() => {
    cleanup();
    vi.restoreAllMocks();
});

it("edits an activity inline and submits the four editable fields", async () => {
    const { onUpdate } = renderList();
    onUpdate.mockResolvedValue(undefined);

    fireEvent.click(screen.getByRole("button", { name: "Redigera" }));
    expect(screen.getByLabelText("Time")).toHaveValue("10:00");

    fireEvent.change(screen.getByLabelText("Name"), { target: { value: "Lunch" } });
    fireEvent.change(screen.getByLabelText("Time"), { target: { value: "12:30" } });
    fireEvent.click(screen.getByRole("button", { name: "Spara" }));

    await waitFor(() =>
        expect(onUpdate).toHaveBeenCalledWith(100, {
            name: "Lunch",
            activityDate: "2026-07-03",
            startTime: "12:30",
            description: "Kaffe",
        }),
    );
});

it("cancels editing without calling the API", () => {
    const { onUpdate } = renderList();

    fireEvent.click(screen.getByRole("button", { name: "Redigera" }));
    fireEvent.change(screen.getByLabelText("Name"), { target: { value: "Ändrat" } });
    fireEvent.click(screen.getByRole("button", { name: "Avbryt" }));

    expect(onUpdate).not.toHaveBeenCalled();
    expect(screen.getByText("Fika")).toBeInTheDocument();
});

it("deletes an activity after confirmation", async () => {
    vi.spyOn(window, "confirm").mockReturnValue(true);
    const { onDelete } = renderList();
    onDelete.mockResolvedValue(undefined);

    fireEvent.click(screen.getByRole("button", { name: "Ta bort" }));

    await waitFor(() => expect(onDelete).toHaveBeenCalledWith(100));
});

it("does not delete when confirmation is cancelled", () => {
    vi.spyOn(window, "confirm").mockReturnValue(false);
    const { onDelete } = renderList();

    fireEvent.click(screen.getByRole("button", { name: "Ta bort" }));

    expect(onDelete).not.toHaveBeenCalled();
});
