import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { api, ApiError, clearCsrf } from "../api/ApiClient";
import { createUser, type UserRequestDTO, type UserResponseDTO } from "../features/users/UserFetch";
export type AuthUser = UserResponseDTO;
interface AuthState {
    user: AuthUser | null; loading: boolean; error: string;
    login: (email: string, password: string) => Promise<void>;
    register: (data: UserRequestDTO) => Promise<void>;
    logout: () => Promise<void>;
}
const Context = createContext<AuthState | null>(null);
export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<AuthUser | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    useEffect(() => {
        let active = true;
        const expired = () => { setUser(null); clearCsrf(); };
        window.addEventListener("session-expired", expired);
        api<AuthUser>("/api/auth/me").then(value => { if (active) setUser(value); })
            .catch(err => { if (active && !(err instanceof ApiError && err.status === 401)) setError("Kunde inte hämta sessionen. Ladda om sidan."); })
            .finally(() => { if (active) setLoading(false); });
        return () => { active = false; window.removeEventListener("session-expired", expired); };
    }, []);
    async function authenticate(path: string, data: unknown) {
        const value = await api<AuthUser>(path, { method: "POST", body: JSON.stringify(data) });
        clearCsrf(); setError(""); setUser(value);
    }
    return <Context.Provider value={{ user, loading, error,
        login: (email, password) => authenticate("/api/auth/login", { email, password }),
        register: async data => {
            const user = await createUser(data);
            clearCsrf();
            setError("");
            setUser(user);
        },
        logout: async () => { await api<void>("/api/auth/logout", { method: "POST" }); clearCsrf(); setUser(null); },
    }}>{children}</Context.Provider>;
}
// Context and its hook share a module so consumers use the same instance.
// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
    const value = useContext(Context);
    if (!value) throw new Error("AuthProvider is required");
    return value;
}
