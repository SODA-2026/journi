export interface UserRequestDTO {
  email: string;
  name: string;
  password: string;
  profileImage: string;
}

export interface UserResponseDTO {
  id?: number;
  email: string;
  name: string;
  profileImage: string;
}



export async function createUser(data: UserRequestDTO): Promise<UserResponseDTO> {
  const response = await fetch(`http://localhost:8080/users/create`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    throw new Error(`Server error: ${response.status}`);
  }

  return response.json();
}