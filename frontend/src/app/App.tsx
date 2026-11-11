import "../App.css";
import { AuthProvider } from "../context/AuthContext";

import { NavBar } from "../shared/NavBar";
import { AppRoutes } from "./AppRoutes";

function App() {
    return (
        <AuthProvider>
            <NavBar />
            <AppRoutes />
        </AuthProvider>
    );
}

export default App;
