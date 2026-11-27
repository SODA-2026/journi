import { useAuth } from "../context/AuthContext";
import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router";
import logo from "../images/icons/journi-icon-logo.png"
import home from "../images/icons/home.png"
import homeColored from "../images/icons/home-colored.png"

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
        <header className="">
            <nav className="flex items-center justify-between border">
                <div className="flex justify-between border">
                    <Link to="/" className="text-2xl font-bold text-[#1d4237] flex items-center p-4 gap-2">
                        <img src={logo} alt="Journi-logo" className="w-20 h-20 border" />
                        Journi
                    </Link>
                    <div className="ml-15 flex gap-10 items-center p-4 text-lg">
                        <Link to="/" className="flex group items-center border-b border-transparent gap-2 p-2 hover:border-current hover:text-amber-600">
                            <img src={home} className="absolute w-6 h-6 group-hover:opacity-0" />
                            <img src={homeColored} className="opacity-0 w-6 h-6 group-hover:opacity-100" />
                            Hem
                        </Link>
                        <Link to="" className="border-b border-transparent p-2 hover:border-current hover:text-amber-600">
                            Utforska
                        </Link>
                        <Link to="" className="border-b border-transparent p-2 hover:border-current hover:text-amber-600">
                            Om Journi
                        </Link>
                    </div>
                </div>

                <div className="border">
                    <div className="flex flex-wrap items-center gap-2">
                        {/* <NavLink to="/trips" end className={navLinkClass}>
                            Mina resor
                        </NavLink> */}
                        {/* <NavLink to="/trips/new" className={navLinkClass}>
                            Skapa resa
                        </NavLink> */}
                        {user ? <button onClick={() => { logout().then(() => navigate("/")).catch(() => setError("Kunde inte logga ut")); }}>Logga ut ({user.name})</button> : <>
                        <NavLink to="/login" className={navLinkClass}>Logga in</NavLink>
                        <NavLink to="/register" className={navLinkClass}>
                            Skapa konto
                        </NavLink>
                        </>}
                        {error && <p role="alert">{error}</p>}
                    </div>
                </div>
            </nav>
        </header>
    );
}
