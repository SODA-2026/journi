import { Navigate, Outlet, Route, Routes } from "react-router";
import { useAuth } from "../context/AuthContext";

import { HomePage } from "./HomePage";
import { LoginForm } from "../features/users/LoginForm";
import { CreateAccountPage } from "../features/users/RegisterPage";
import { MyDashboard } from "../pages/MyDashboard";
import { MyTripsPage } from "../pages/MyTripsPage";
import { CreateTripPage } from "../pages/CreateTripPage";
import { TripDetailsPage } from "../pages/TripDetailsPage";

import { NavBar } from "../shared/NavBar";
import { AppLayout } from "../shared/layout/AppLayout";

// Public pages use the top navigation.
function PublicLayout() {
    return (
        <>
            <NavBar />
            <Outlet />
        </>
    );
}

// Wait for the session check before showing protected pages.
function Protected() {
    const { user, loading, error } = useAuth();

    if (loading) {
        return <p role="status">Laddar session...</p>;
    }

    if (error) {
        return <p role="alert">{error}</p>;
    }

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    return <Outlet />;
}

export function AppRoutes() {
    return (
        <Routes>
            {/* Public pages with NavBar */}
            <Route element={<PublicLayout />}>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginForm />} />
                <Route path="/register" element={<CreateAccountPage />} />
            </Route>

            {/* Pages requiring login, with the shared sidebar */}
            <Route element={<Protected />}>
                <Route element={<AppLayout />}>
                    <Route path="/dashboard" element={<MyDashboard />} />
                    <Route path="/trips" element={<MyTripsPage />} />
                    <Route path="/trips/new" element={<CreateTripPage />} />
                    <Route path="/trips/:id" element={<TripDetailsPage />} />
                </Route>
            </Route>
        </Routes>
    );
}
