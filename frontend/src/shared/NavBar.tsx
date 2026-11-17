import { useAuth } from "../context/AuthContext";
import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router";

function navLinkClass({ isActive }: { isActive: boolean }) {
    return isActive
        ? "rounded-lg bg-[#235e4f] px-4 py-2 text-white"
        : "rounded-lg px-4 py-2 text-[#1d4237] hover:bg-[#235e4f]/10";
}

export function NavBar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [error, setError] = useState("");
    return (
        <header className="border-b border-[#9fb0aa]/50 bg-white">
            <nav className="mx-auto flex max-w-5xl flex-wrap items-center justify-between gap-4 px-6 py-4">
                <Link to="/" className="text-2xl font-bold text-[#1d4237]">
                    Journi
                </Link>

                <div className="flex flex-wrap items-center gap-2">
                    <NavLink to="/trips" end className={navLinkClass}>
                        Mina resor
                    </NavLink>
                    <NavLink to="/trips/new" className={navLinkClass}>
                        Skapa resa
                    </NavLink>
                    {user ? <button onClick={() => { logout().then(() => navigate("/")).catch(() => setError("Kunde inte logga ut")); }}>Logga ut ({user.name})</button> : <>
                    <NavLink to="/login" className={navLinkClass}>Logga in</NavLink>
                    <NavLink to="/register" className={navLinkClass}>
                        Skapa konto
                    </NavLink>
                    </>}
                    {error && <p role="alert">{error}</p>}
                </div>
            </nav>
        </header>
    );
}
