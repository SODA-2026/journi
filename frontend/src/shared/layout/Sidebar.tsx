import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router";
import { LayoutDashboard, Luggage, LogOut, UserRound, Plus } from "lucide-react";
import { useAuth } from "../../context/AuthContext";

interface SidebarProps {
    collapsed: boolean;
    onNavigate?: () => void;
}

const navigationLinks = [
    { to: "/dashboard", label: "Översikt", Icon: LayoutDashboard },
    { to: "/trips", label: "Mina resor", Icon: Luggage },
    { to: "/trips/new", label: "Skapa resa", Icon: Plus },
];

export function Sidebar({ collapsed, onNavigate }: SidebarProps) {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [isLoggingOut, setIsLoggingOut] = useState(false);
    const [logoutError, setLogoutError] = useState("");

    // Log out and return to the home page
    async function signOut() {
        setIsLoggingOut(true);
        setLogoutError("");

        try {
            await logout();
            navigate("/");
        } catch {
            setLogoutError("Kunde inte logga ut. Försök igen.");
        } finally {
            setIsLoggingOut(false);
        }
    }

    return (
        <div className="flex h-full flex-col gap-6 p-3">
            {/* App name */}
            <div className="flex items-center justify-between gap-2 px-1 py-3">
                <Link
                    to="/dashboard"
                    onClick={onNavigate}
                    aria-label="Journi – översikt"
                    title="Journi"
                    className="text-2xl font-bold"
                >
                    {collapsed ? "J" : "Journi"}
                </Link>
            </div>

            {/* Navigation links */}
            <nav aria-label="Huvudnavigation" className="flex flex-col gap-2">
                {navigationLinks.map(({ to, label, Icon }) => (
                    <NavLink
                        key={to}
                        to={to}
                        end
                        onClick={onNavigate}
                        aria-label={label}
                        title={collapsed ? label : undefined}
                        className={({ isActive }) =>
                            `flex items-center gap-3 rounded-xl p-3 ${
                                isActive
                                    ? "bg-[#36745e] text-white"
                                    : "text-[#d4e4db] hover:bg-white/10"
                            }`
                        }
                    >
                        <Icon size={20} aria-hidden="true" className="shrink-0" />
                        {!collapsed && <span>{label}</span>}
                    </NavLink>
                ))}
            </nav>

            <div className="mt-auto border-t border-white/15 pt-4">
                {/* User profile */}
                <div className="mb-3 flex items-center gap-3 p-3" title={user?.name}>
                    <UserRound size={20} aria-hidden="true" className="shrink-0" />
                    {!collapsed && <span className="truncate">{user?.name}</span>}
                </div>

                {/* Log out */}
                <button
                    type="button"
                    disabled={isLoggingOut}
                    onClick={signOut}
                    aria-label="Logga ut"
                    title={collapsed ? "Logga ut" : undefined}
                    className="flex w-full items-center gap-3 rounded-xl p-3 text-left hover:bg-white/10 disabled:opacity-50"
                >
                    <LogOut size={20} aria-hidden="true" className="shrink-0" />
                    {!collapsed && <span>{isLoggingOut ? "Loggar ut…" : "Logga ut"}</span>}
                </button>

                {logoutError && (
                    <p role="alert" className="mt-2 text-sm text-red-200">
                        {logoutError}
                    </p>
                )}
            </div>
        </div>
    );
}
