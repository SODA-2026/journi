import React, { useState } from 'react';
import { createUser, type UserRequestDTO } from '../api/UserFetch';


export function AddUserForm() {
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
    try {
      const savedUser = await createUser(formData);
      alert('User Created Successfully!');
      console.log('Saved User Response:', savedUser);
    } catch (err) {
      console.error('Failed to create user:', err);
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
        value={formData.password}
        onChange={handleChange}
        placeholder="Password"
      />
      <input
        type="text"
        name="profileImage"
        value={formData.profileImage}
        onChange={handleChange}
        placeholder="Profile Image URL"
      />
      <button type="submit">Submit</button>
    </form>
  );
}