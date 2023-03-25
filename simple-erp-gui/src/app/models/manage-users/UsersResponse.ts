import {UserListItem} from "./UserListItem";

export interface UsersResponse{
  userList: UserListItem[];
  totalUsersLength: number;
}
