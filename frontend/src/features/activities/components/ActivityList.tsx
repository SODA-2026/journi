import { useState, type ChangeEvent } from "react";
import type { ActivityResponse, UpdateActivityRequest, ActivityFormErrors } from "../ActivitiesTypes";
import { ApiError } from "../../../api/ApiClient";
import {
    validateEditableFields,
    type EditableActivityFields,
} from "../ActivityValidation";

interface DestinationOption {
    id: number;
    label: string;
}

interface ActivityListProps {
    activities: ActivityResponse[];
    destinations: DestinationOption[];
    tripStartDate?: string;
    tripEndDate?: string;
    onUpdate: (id: number, data: UpdateActivityRequest) => Promise<void>;
    onDelete: (id: number) => Promise<void>;
}

const emptyDraft: EditableActivityFields = {
    name: "",
    activityDate: "",
    startTime: "",
    description: "",
};

function draftFromActivity(a: ActivityResponse): EditableActivityFields {
    return {
        name: a.name,
        activityDate: a.activityDate ?? "",
        // Backend returns "14:00:00"; an <input type="time"> needs "14:00"
        startTime: a.startTime ? a.startTime.slice(0, 5) : "",
        description: a.description ?? "",
    };
}

// Activities without time are placed last within their day
const sortKey = (a: ActivityResponse) =>
    `${a.activityDate} ${a.startTime ? a.startTime.slice(0, 5) : "99:99"}`;

export function ActivityList({
    activities,
    destinations, 
    tripStartDate, 
    tripEndDate, 
    onUpdate,
    onDelete,
}: ActivityListProps) {
    const [editingId, setEditingId] = useState<number | null>(null);
    const [draft, setDraft] = useState<EditableActivityFields>(emptyDraft);
    const [saving, setSaving] = useState(false);
    const [editError, setEditError] = useState("");
    const [fieldErrors, setFieldErrors] = useState<ActivityFormErrors>({});
    const [deletingId, setDeletingId] = useState<number | null>(null);
    const [deleteError, setDeleteError] = useState<{
        id: number;
        message: string;
    } | null>(null);

    if (activities.length === 0) {
        return (
            <p className="rounded-xl border border-dashed border-[#9fb0aa] px-4 py-6 text-center text-sm text-[#526c62]">
                No activities added yet.
            </p>
        );
    }

    const handleDraftChange = (
        e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
    ) => {
        const { name, value } = e.target;
        setDraft((prev) => ({ ...prev, [name]: value }));
        setFieldErrors((prev) => ({ ...prev, [name]: undefined }));
        setEditError("");
    };

    const startEditing = (a: ActivityResponse) => {
        setDraft(draftFromActivity(a));
        setEditError("");
        setEditingId(a.id);
        setFieldErrors({});
    };

    const cancelEditing = () => {
        setEditingId(null);
        setEditError("");
        setFieldErrors({});
    };

    const handleSave = async (a: ActivityResponse) => {
        const data = { ...draft, name: draft.name.trim() };
        const errors = validateEditableFields(data, tripStartDate, tripEndDate);
        setFieldErrors(errors);
        if (Object.keys(errors).length > 0) return;

        setSaving(true);
        setEditError("");
        try {
            await onUpdate(a.id, {
                name: data.name,
                activityDate: data.activityDate,
                startTime: data.startTime || null,
                description: data.description || null,
            });
            setEditingId(null);
        } catch (err) {
            console.error("Failed to update activity:", err);
            setEditError(err instanceof ApiError && err.status === 400
                ? err.message
                : "Kunde inte spara aktiviteten");
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async (id: number) => {
        if (!window.confirm("Vill du ta bort aktiviteten?")) return;

        setDeletingId(id);
        setDeleteError(null);
        try {
            await onDelete(id);
        } catch (err) {
            console.error("Failed to delete activity:", err);
            setDeleteError({ id, message: "Kunde inte ta bort aktiviteten" });
        } finally {
            setDeletingId(null);
        }
    };

    // Rearranges the original array in place
    // localeCompare compares the strings so that the times are in order
    const sorted = [...activities].sort((a, b) =>
        sortKey(a).localeCompare(sortKey(b)),
    );

    // Group activities by date
    const byDate = new Map<string, ActivityResponse[]>();

    for (const activity of sorted) {
        const list = byDate.get(activity.activityDate) ?? [];
        list.push(activity);
        byDate.set(activity.activityDate, list);
    }

    // Finds the destination name ie. Malmö
    const destinationLabel = (id: number) =>
        destinations.find((d) => d.id === id)?.label ?? "";

    return (
        <div className="flex flex-col gap-4">
            {/* this map creates one section per date */}
            {[...byDate.entries()].map(([Date, items]) => (
                <section
                    key={Date}
                    className="rounded-xl border border-[#9fb0aa] bg-white px-4 py-4 sm:px-5"
                >
                    <h3 className="mb-3 text-lg font-bold text-[#1d4237]">
                        {Date}
                    </h3>
                    <ul className="flex flex-col divide-y divide-[#d6e1d9]">
                        {/* this map creates one list item per activity */}
                        {items.map((a) => (
                            <li
                                key={a.id}
                                className="flex flex-col gap-1 py-3 first:pt-0 last:pb-0"
                            >
                                {editingId === a.id ? (
                                    <div className="flex flex-col gap-2">
                                        <input
                                            aria-label="Name"
                                            name="name"
                                            value={draft.name}
                                            onChange={handleDraftChange}
                                            className="w-full rounded-lg border border-[#9fb0aa] px-3 py-2 text-base text-[#1d4237]"
                                        />
                                        <input
                                            aria-label="Date"
                                            type="date"
                                            name="activityDate"
                                            min={tripStartDate}
                                            max={tripEndDate}
                                            aria-invalid={!!fieldErrors.activityDate}
                                            aria-describedby="activityDate-error-inline"
                                            value={draft.activityDate}
                                            onChange={handleDraftChange}
                                            className="w-full rounded-lg border border-[#9fb0aa] px-3 py-2 text-base text-[#1d4237]"
                                        />

                                        {fieldErrors.activityDate && (
                                            <p id="activityDate-error-inline" role="alert" className="text-sm text-red-700">
                                                {fieldErrors.activityDate}
                                            </p>
                                        )}
                                        <input
                                            aria-label="Time"
                                            type="time"
                                            name="startTime"
                                            value={draft.startTime}
                                            onChange={handleDraftChange}
                                            className="w-full rounded-lg border border-[#9fb0aa] px-3 py-2 text-base text-[#1d4237]"
                                        />
                                        <textarea
                                            aria-label="Description"
                                            name="description"
                                            rows={2}
                                            value={draft.description}
                                            onChange={handleDraftChange}
                                            className="w-full resize-y rounded-lg border border-[#9fb0aa] px-3 py-2 text-base text-[#1d4237]"
                                        />
                                        {editError && (
                                            <p role="alert" className="text-sm text-red-700">
                                                {editError}
                                            </p>
                                        )}
                                        <div className="flex gap-2">
                                            <button
                                                type="button"
                                                onClick={() => handleSave(a)}
                                                disabled={saving}
                                                className="rounded-lg bg-[#235e4f] px-4 py-2 text-white hover:bg-[#1d4237] disabled:opacity-50"
                                            >
                                                {saving ? "Sparar..." : "Spara"}
                                            </button>
                                            <button
                                                type="button"
                                                onClick={cancelEditing}
                                                disabled={saving}
                                                className="rounded-lg border border-[#9fb0aa] px-4 py-2 text-[#1d4237] disabled:opacity-50"
                                            >
                                                Avbryt
                                            </button>
                                        </div>
                                    </div>
                                ) : (
                                    <>
                                        <div className="flex flex-wrap items-baseline gap-x-3 gap-y-1">
                                            <strong className="text-[#235e4f]">
                                                {/* Slice(0,5) gör om 14:00:00 till 14:00 */}
                                                {a.startTime ? a.startTime.slice(0, 5) : "No time"}
                                            </strong>{" "}
                                            <span className="font-semibold text-[#1d4237]">
                                                {a.name}
                                            </span>
                                            {destinationLabel(a.tripDestinationId) && (
                                                <span className="text-sm text-[#526c62]">
                                                    ({destinationLabel(a.tripDestinationId)})
                                                </span>
                                            )}
                                        </div>
                                        {a.description && (
                                            <p className="text-[#526c62]">{a.description}</p>
                                        )}
                                        <div className="flex gap-2">
                                            <button
                                                type="button"
                                                onClick={() => startEditing(a)}
                                                disabled={deletingId === a.id}
                                                className="rounded-lg border border-[#9fb0aa] px-3 py-1 text-sm text-[#1d4237] hover:border-[#235e4f] disabled:opacity-50"
                                            >
                                                Redigera
                                            </button>
                                            <button
                                                type="button"
                                                onClick={() => handleDelete(a.id)}
                                                disabled={deletingId === a.id}
                                                className="rounded-lg border border-red-600 px-3 py-1 text-sm text-red-700 hover:bg-red-600 hover:text-white disabled:opacity-50"
                                            >
                                                {deletingId === a.id ? "Tar bort..." : "Ta bort"}
                                            </button>
                                        </div>
                                        {deleteError?.id === a.id && (
                                            <p role="alert" className="text-sm text-red-700">
                                                {deleteError.message}
                                            </p>
                                        )}
                                    </>
                                )}
                                <p>
                                    {a.createdByName
                                        ? `Skapad av ${a.createdByName}`
                                        : "Okänd skapare"
                                    }

                                </p>

                            </li>
                        ))}
                    </ul>
                </section>
            ))}
        </div>
    );
}
