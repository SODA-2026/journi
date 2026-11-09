import { Route, Routes } from "react-router";
import { HomePage } from "./HomePage";
import { CreateTripPage } from "../pages/CreateTripPage";
import { MyTripsPage } from "../pages/MyTripsPage";
import { CreateAccountPage } from "../features/users/RegisterPage";

export function AppRoutes() {
    return (
        <Routes>
            <Route path="/" element={<HomePage />} />
            <Route
                path="/trips/new"
                element={<CreateTripPage />}
            />
            <Route path="/trips" element={<MyTripsPage />} />
            <Route
                path="/register"
                element={<CreateAccountPage />}
            />
        </Routes>
    );
}
