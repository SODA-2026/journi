import "./App.css";
import { useState } from "react";

import { AddUserForm } from "./components/AddUserForm";
import { DestinationSelect } from "./components/DestinationSelect";
import { TripForm } from "./components/TripForm";
import { DeleteTripButton } from "./components/DeleteTripButton";

function App() {
  const [, setCountryId] = useState(0);
  const [, setCityId] = useState(0);

  return (
    <>
      <AddUserForm />
      <TripForm />
      <DeleteTripButton
          tripId={13}
          onDeleted={() => {
              alert("Trip deleted!");
          }}
      /> 
    </>
  );
}

export default App;
