import { api } from "../../api/ApiClient";

export interface UserRequestDTO {
  email: string;
  name: string;
  password: string;
  profileImage: string;
}

export interface UserResponseDTO {
  id: number;
  email: string;
  name: string;
}
export function createUser(data: UserRequestDTO): Promise<UserResponseDTO> {
  return api("/api/auth/register", {
    method: "POST",
    body: JSON.stringify(data),
  });
}
