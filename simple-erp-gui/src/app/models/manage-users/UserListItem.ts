export interface UserListItem {
  id: number;
  username: string;
  email: string;
  name: string;
  surname: string;
  phone?: string;
  roles: string[];
}
