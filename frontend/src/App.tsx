import "./App.css";
import { useState } from "react";

import { AddUserForm } from "./components/AddUserForm";
import { TripForm } from "./components/TripForm";
import { TripList } from "./components/TripList";
import { NavBar } from "./components/NavBar";
import { AppRoutes } from "./routes/AppRoutes";

function App() {
    const [, setCountryId] = useState(0);
    const [, setCityId] = useState(0);

    return (
        <>
            <NavBar></NavBar>
            <AppRoutes></AppRoutes>
        </>
    );
}

export default App;
