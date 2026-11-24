import type { TripDestination } from "../trips/trips";
import { api } from "../../api/ApiClient";

export interface Invitation {
    id: number;
    tripId: number;
    tripTitle: string;
    inviterName: string;
    startDate: string;
    endDate: string;
    destinations: TripDestination[];
    status: "PENDING" | "ACCEPTED" | "DECLINED";
}

export interface SentInvitation {
    id: number;
    tripId: number;
    tripTitle: string;
    inviteeEmail: string;
    status: Invitation["status"];
}

export function getSentInvitations(): Promise<SentInvitation[]> {
    return api("/invitations/sent");
}

export function inviteUser(tripId: number, email: string): Promise<Invitation> {
    return api("/invitations", {
        method: "POST",
        body: JSON.stringify({ tripId, email }),
    });
}

export function getIncomingInvitations(): Promise<Invitation[]> {
    return api("/invitations/mine");
}

export function respondToInvitation(id: number, accept: boolean): Promise<Invitation> {
    return api(`/invitations/${id}/${accept ? "accept" : "decline"}`, {
        method: "POST",
    });
}
