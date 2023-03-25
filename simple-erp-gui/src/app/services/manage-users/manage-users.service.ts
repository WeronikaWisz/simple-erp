import { Injectable } from '@angular/core';
import {AddUserRequest} from "../../models/auth/AddUserRequest";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";

const AUTH_API = 'http://localhost:8080/manage-users/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};


@Injectable({
  providedIn: 'root'
})
export class ManageUsersService {

  constructor(private http: HttpClient) { }

  addUser(addUserRequest: AddUserRequest): Observable<any> {
    return this.http.post(AUTH_API + 'add-user', JSON.stringify(addUserRequest), httpOptions);
  }
}
