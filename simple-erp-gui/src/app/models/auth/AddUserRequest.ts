export interface AddUserRequest {
  username: string;
  email: string;
  password: string;
  name: string;
  surname: string;
  phone?: string;
  roles: string[];
}
