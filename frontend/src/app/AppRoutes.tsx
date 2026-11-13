import { useAuth } from "../context/AuthContext";
import { LoginForm } from "../features/users/LoginForm";
import { Navigate, Outlet, Route, Routes } from "react-router";
import { HomePage } from "./HomePage";
import { CreateTripPage } from "../pages/CreateTripPage";
import { MyTripsPage } from "../pages/MyTripsPage";
import { CreateAccountPage } from "../features/users/RegisterPage";
import { TripDetailsPage } from "../pages/TripDetailsPage";

function Protected() {
    const { user, loading, error } = useAuth();
    if (loading) return <p>Laddar session...</p>;
    if (error) return <p role="alert">{error}</p>;
    return user ? <Outlet /> : <Navigate to="/login" replace />;
}
export function AppRoutes() {
    return (
        <Routes>
            <Route path="/" element={<HomePage />} />
            <Route element={<Protected />}>
                <Route path="/trips/new" element={<CreateTripPage />} />
                <Route path="/trips" element={<MyTripsPage />} />
            </Route>
            <Route path="/login" element={<LoginForm />} />
            <Route path="/register" element={<CreateAccountPage />} />
            <Route
                path="/trips/:id"
                element={<TripDetailsPage></TripDetailsPage>}
            ></Route>
        </Routes>
    );
}
