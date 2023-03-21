import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ChangePassword} from "../../models/auth/ChangePassword";
import { Observable } from 'rxjs';

const USERS_API = 'http://localhost:8080/users/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  constructor(private http: HttpClient) { }

  changePassword(changePassword: ChangePassword): Observable<any>{
    return this.http.put(USERS_API + 'user/password', JSON.stringify(changePassword), httpOptions);
  }

}
