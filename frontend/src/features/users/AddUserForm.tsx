import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router";
import React, { useState } from 'react';
import { type UserRequestDTO } from "./UserFetch";


export function AddUserForm() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [pending, setPending] = useState(false);
  const [formData, setFormData] = useState<UserRequestDTO>({
    email: '',
    name: '',
    password: '',
    profileImage: '',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setPending(true);
    setError("");
    try {
      await register(formData);
      navigate("/trips");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Kunde inte skapa konto");
    } finally {
      setPending(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        name="name"
        value={formData.name}
        onChange={handleChange}
        placeholder="Name"
      />
      <input
        type="email"
        name="email"
        value={formData.email}
        onChange={handleChange}
        placeholder="Email"
      />
      <input
        type="password"
        name="password"
        aria-describedby="password-requirements"
        value={formData.password}
        onChange={handleChange}
        placeholder="Password"
      />
      <p id="password-requirements">Lösenordet måste innehålla minst 8 tecken.</p>
      <input
        type="text"
        name="profileImage"
        value={formData.profileImage}
        onChange={handleChange}
        placeholder="Profile Image URL"
      />
      <button type="submit" disabled={pending}>Submit</button>
      {error && <p role="alert">{error}</p>}
    </form>
  );
}
