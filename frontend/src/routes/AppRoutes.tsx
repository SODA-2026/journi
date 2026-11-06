import { Route, Routes } from "react-router";
import { CreateTripPage } from "../pages/CreateTripPage";
import { MyTripsPage } from "../pages/MyTripsPage";
import { CreateAccountPage } from "../pages/RegisterPage";
import { HomePage } from "../pages/HomePage";

export function AppRoutes() {
    return (
        <Routes>
            <Route path="/" element={<HomePage></HomePage>}></Route>
            <Route
                path="/trips/new"
                element={<CreateTripPage></CreateTripPage>}
            ></Route>
            <Route path="/trips" element={<MyTripsPage></MyTripsPage>}></Route>
            <Route
                path="/register"
                element={<CreateAccountPage></CreateAccountPage>}
            ></Route>
        </Routes>
    );
}
