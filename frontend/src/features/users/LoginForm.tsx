import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router";
import { useAuth } from "../../context/AuthContext";

export function LoginForm() {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [error, setError] = useState("");
    const [pending, setPending] = useState(false);

    async function submit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        const data = new FormData(event.currentTarget);
        setPending(true);
        setError("");
        try {
            await login(String(data.get("email")), String(data.get("password")));
            navigate("/trips");
        } catch (err) {
            setError(err instanceof TypeError ? "Kunde inte ansluta till servern. Försök igen senare."
                : err instanceof Error ? err.message : "Kunde inte logga in");
        } finally {
            setPending(false);
        }
    }

    return <form onSubmit={submit}>
            <h1>Logga in</h1>
            <fieldset disabled={pending}>
                <label htmlFor="login-email">E-post</label>
                <input id="login-email" name="email" type="email" autoComplete="username" required />
                <label htmlFor="login-password">Lösenord</label>
                <input id="login-password" name="password" type="password" autoComplete="current-password" required />
                <button type="submit">{pending ? "Loggar in…" : "Logga in"}</button>
            </fieldset>
            {error && <p role="alert">{error}</p>}
            <p>Inget konto? <Link to="/register">Skapa konto</Link></p>
    </form>;
}
