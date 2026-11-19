
import type { ActivityResponse } from "../ActivitiesTypes";

interface DestinationOption {
    id: number;
    label: string;
} 

interface ActivityListProps {
    activities: ActivityResponse[];
    destinations: DestinationOption[];
}

// Activities without time are placed last wihtin their day
const sortKey = (a: ActivityResponse) => 
    `${a.activityDate} ${a.startTime ? a.startTime.slice(0,5) : "99:99"}`


export function ActivityList({ activities, destinations}: ActivityListProps){
    if(activities.length === 0) {
        return (
            <p className="rounded-xl border border-dashed border-[#9fb0aa] px-4 py-6 text-center text-sm text-[#526c62]">
                No activities added yet.
            </p>
        );
    }

    // Rearranges the original array in place 
    // localCompare compares the strings so that the time are in order
    const sorted = [...activities].sort((a,b) => 
        sortKey(a).localeCompare(sortKey(b))
    )

    // Group activities by date
    const byDate = new Map<string, ActivityResponse[]>()

    for(const activity of sorted) {
        const list = byDate.get(activity.activityDate) ?? []
        list.push(activity)
        byDate.set(activity.activityDate, list)
    }

    // Finds the destination name ie. Malmö
    const destinationLabel = (id: number) => 
        destinations.find((d) => d.id === id)?.label ?? ""

    return(
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
                            <li key={a.id} className="flex flex-col gap-1 py-3 first:pt-0 last:pb-0">
                                <div className="flex flex-wrap items-baseline gap-x-3 gap-y-1">
                                <strong className="text-[#235e4f]">
                                    {/* Slice(0,5) gör om 14:00:00 till 14:00 */}
                                    {a.startTime ? a.startTime.slice(0,5) : "No time"}
                                </strong> {" "}
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

                            </li>
                        ))}
                    </ul>

                </section>
            ))}
        </div>
    )
}