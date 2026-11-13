import { useEffect, useRef, useState } from "react";
import { Outlet } from "react-router";
import { Menu, X } from "lucide-react";
import { Sidebar } from "./Sidebar";

export function AppLayout() {
    // Desktop sidebar
    const [expanded, setExpanded] = useState(false);

    // Mobile menu
    const dialog = useRef<HTMLDialogElement>(null);

    function closeMenu() {
        dialog.current?.close();
    }

    useEffect(() => {
        const media = window.matchMedia("(min-width: 768px)");
        const onResize = () => {
            if (media.matches) {
                dialog.current?.close();
            }
        };

        media.addEventListener("change", onResize);
        return () => media.removeEventListener("change", onResize);
    }, []);

    return (
        <div className="min-h-dvh bg-[#f4f7f3] text-[#1d4237]">
            <a href="#page-content" className="skip-link">
                Hoppa till innehåll
            </a>

            {/* Desktop navigation */}
            <aside
                aria-label="Sidomeny"
                data-expanded={expanded}
                onMouseEnter={() => setExpanded(true)}
                onMouseLeave={() => setExpanded(false)}
                onFocus={() => setExpanded(true)}
                onBlur={event => {
                    if (!event.currentTarget.contains(event.relatedTarget)) {
                        setExpanded(false);
                    }
                }}
                className="fixed inset-y-0 left-0 z-20 hidden w-[68px] bg-[#173c31] text-white transition-[width] duration-200 data-[expanded=true]:w-[248px] md:block"
            >
                <Sidebar collapsed={!expanded} />
            </aside>

            {/* Mobile navigation */}
            <header className="flex items-center gap-4 border-b border-[#d6e1d9] bg-white px-4 py-3 md:hidden">
                <button
                    type="button"
                    aria-label="Öppna navigation"
                    aria-haspopup="dialog"
                    onClick={() => dialog.current?.showModal()}
                    className="rounded-lg p-2"
                >
                    <Menu />
                </button>
                <span className="text-xl font-bold">Journi</span>
            </header>

            <dialog
                ref={dialog}
                aria-label="Navigation"
                onClick={event => {
                    if (event.target === event.currentTarget) {
                        closeMenu();
                    }
                }}
                className="fixed inset-0 m-0 h-dvh max-h-none w-full max-w-none bg-transparent p-0 text-white open:flex backdrop:bg-black/40 md:open:hidden"
            >
                <div className="flex h-full w-[min(85vw,300px)] flex-col bg-[#173c31]">
                    <button
                        type="button"
                        onClick={closeMenu}
                        aria-label="Stäng navigation"
                        className="m-3 self-end rounded-lg p-2 hover:bg-white/10"
                    >
                        <X />
                    </button>
                    <Sidebar collapsed={false} onNavigate={closeMenu} />
                </div>
            </dialog>

            {/* Shared page content */}
            <div className="md:pl-[68px]">
                <main
                    id="page-content"
                    tabIndex={-1}
                    className="mx-auto max-w-6xl px-4 py-8 sm:px-8 lg:py-12"
                >
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
