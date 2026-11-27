import { useAuth } from "../context/AuthContext";
import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router";
import logo from "../images/icons/journi-icon-logo.png"
import home from "../images/icons/home.png"
import home2 from "../images/icons/home2.png"
import homeColored from "../images/icons/home-colored.png"

function navLinkClass({ isActive }: { isActive: boolean }) {
    return isActive
        ? "rounded-full bg-[#235e4f] px-4 py-2 text-white"
        : "rounded-full bg-amber-600 px-4 py-2 text-black border hover:bg-amber-300";
}

export function NavBar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [error, setError] = useState("");
    return (
        <header className="absolute inset-x-0 top-0 z-10 bg-linear-to-t from-amber-0 to-gray-800">
            <nav className="flex items-center justify-between">
                <div className="flex justify-between">
                    <Link to="/" className="text-2xl font-bold text-amber-100 flex items-center p-4 gap-2">
                        <img src={logo} alt="Journi-logo" className="w-20 h-20" />
                        Journi
                    </Link>
                    <div className="ml-15 flex gap-10 items-center p-4 text-lg">
                        <Link to="/" className="flex group items-center border-b border-transparent gap-2 p-2 text-amber-100 hover:border-current hover:text-amber-600">
                            <img src={home2} className="absolute w-6 h-6 group-hover:opacity-0" />
                            <img src={homeColored} className="opacity-0 w-6 h-6 group-hover:opacity-100" />
                            Hem
                        </Link>
                        <Link to="" className="border-b border-transparent p-2 text-amber-100 hover:border-current hover:text-amber-600">
                            Utforska
                        </Link>
                        <Link to="" className="border-b border-transparent p-2 text-amber-100 hover:border-current hover:text-amber-600">
                            Om Journi
                        </Link>
                    </div>
                </div>

                <div className="flex flex-wrap items-center gap-4 mr-10">
                    {/* <NavLink to="/trips" end className={navLinkClass}>
                        Mina resor
                    </NavLink> */}
                    {/* <NavLink to="/trips/new" className={navLinkClass}>
                        Skapa resa
                    </NavLink> */}
                    {user ? <button onClick={() => { logout().then(() => navigate("/")).catch(() => setError("Kunde inte logga ut")); }}>Logga ut ({user.name})</button> : <>
                    <NavLink to="/login" className="border border-white rounded-full px-4 py-2 text-white hover:bg-amber-300 hover:text-black hover:border-black">Logga in</NavLink>
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
