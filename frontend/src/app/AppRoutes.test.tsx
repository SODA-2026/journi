import { afterEach, describe, expect, it, vi } from "vitest";
import { cleanup, fireEvent, render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import { AppRoutes } from "./AppRoutes";

const auth = vi.hoisted(() => ({ user: { name: "Olena" } as { name: string } | null, loading: false, error: "", logout: vi.fn() }));
vi.mock("../context/AuthContext", () => ({ useAuth: () => auth }));
vi.mock("../pages/MyTripsPage", () => ({ MyTripsPage: () => <h1>Trips page</h1> }));
vi.mock("../pages/CreateTripPage", () => ({ CreateTripPage: () => <h1>Create page</h1> }));
vi.mock("../features/users/LoginForm", () => ({ LoginForm: () => <h1>Login page</h1> }));
vi.mock("./HomePage", () => ({ HomePage: () => <h1>Home page</h1> }));
vi.stubGlobal("matchMedia", () => ({ matches: false, addEventListener: vi.fn(), removeEventListener: vi.fn() }));
afterEach(() => { cleanup(); auth.user = { name: "Olena" }; });
function open(path: string) { render(<MemoryRouter initialEntries={[path]}><AppRoutes /></MemoryRouter>); }
describe("App layout routing", () => {
    it("expands on hover and collapses after a focused link is clicked", () => {
        open("/trips");
        const sidebar = screen.getByRole("complementary", { name: "Sidomeny" });
        fireEvent.mouseEnter(sidebar);
        expect(sidebar.getAttribute("data-expanded")).toBe("true");
        const link = screen.getByRole("link", { name: "Mina resor" });
        fireEvent.focus(link);
        fireEvent.click(link);
        fireEvent.mouseLeave(sidebar);
        expect(sidebar.getAttribute("data-expanded")).toBe("false");
        expect(link.getAttribute("aria-current")).toBe("page");
    });
    it("redirects unauthenticated users without showing the sidebar", () => {
        auth.user = null; open("/dashboard");
        expect(screen.getByRole("heading", { name: "Login page" })).toBeTruthy();
        expect(screen.queryByRole("navigation", { name: "Huvudnavigation" })).toBeNull();
    });
    it("keeps public pages outside the app layout", () => {
        open("/");
        expect(screen.getByRole("heading", { name: "Home page" })).toBeTruthy();
        expect(screen.queryByRole("navigation", { name: "Huvudnavigation" })).toBeNull();
    });
});
