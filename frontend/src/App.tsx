import "./App.css";
import { useState } from "react";

import { AddUserForm } from "./components/AddUserForm";
import { DestinationSelect } from "./components/DestinationSelect";
import { TripForm } from "./components/TripForm";

function App() {
  const [, setCountryId] = useState(0);
  const [, setCityId] = useState(0);

  return (
    <>
      <AddUserForm />
      <TripForm />
    </>
  );
}

export default App;
