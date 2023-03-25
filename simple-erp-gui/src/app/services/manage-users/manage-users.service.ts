import { Injectable } from '@angular/core';
import {AddUserRequest} from "../../models/auth/AddUserRequest";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {UsersResponse} from "../../models/manage-users/UsersResponse";
import {UserListItem} from "../../models/manage-users/UserListItem";
import {ProfileData} from "../../models/manage-users/ProfileData";
import {UpdateUserData} from "../../models/manage-users/UpdateUserData";
import {UpdateUserRequest} from "../../models/auth/UpdateUserRequest";
import {DefaultUser} from "../../models/manage-users/DefaultUser";
import {UpdateDefaultUserRequest} from "../../models/manage-users/UpdateDefaultUserRequest";
import {UserName} from "../../models/manage-users/UserName";

const MANAGE_USERS_API = 'http://localhost:8080/manage-users/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};


@Injectable({
  providedIn: 'root'
})
export class ManageUsersService {

  constructor(private http: HttpClient) { }

  addUser(addUserRequest: AddUserRequest): Observable<any> {
    return this.http.post(MANAGE_USERS_API + 'user', JSON.stringify(addUserRequest), httpOptions);
  }

  loadUsers(pageIndex: number, pageSize: number): Observable<UsersResponse> {
    return this.http.get<UsersResponse>(MANAGE_USERS_API + `users?page=${pageIndex}&size=${pageSize}`, httpOptions);
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(MANAGE_USERS_API + 'user/' + id)
  }

  getUser(id: number): Observable<UserListItem> {
    return this.http.get<UserListItem>(MANAGE_USERS_API + 'user/' + id, httpOptions);
  }

  updateUser(updateUserRequest: UpdateUserRequest): Observable<any> {
    return this.http.put(MANAGE_USERS_API + 'user', JSON.stringify(updateUserRequest), httpOptions);
  }

  loadDefaultUsers(pageIndex: number, pageSize: number): Observable<DefaultUser[]> {
    return this.http.get<DefaultUser[]>(MANAGE_USERS_API + `default-users?page=${pageIndex}&size=${pageSize}`, httpOptions);
  }

  updateDefaultUser(updateDefaultUserRequest: UpdateDefaultUserRequest): Observable<any> {
    return this.http.put(MANAGE_USERS_API + 'default-user', JSON.stringify(updateDefaultUserRequest), httpOptions);
  }

  loadUserForTask(id: number): Observable<UserName[]> {
    return this.http.get<UserName[]>(MANAGE_USERS_API + 'users-task/' + id, httpOptions);
  }

}
