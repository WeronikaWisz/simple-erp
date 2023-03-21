import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {LoginRequest} from "../../models/auth/LoginRequest";

const AUTH_API = 'http://localhost:8080/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  login(loginRequest: LoginRequest): Observable<any> {
    return this.http.post(AUTH_API + 'signin', JSON.stringify(loginRequest), httpOptions);
  }

}
