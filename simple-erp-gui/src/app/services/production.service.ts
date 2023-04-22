import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {DelegatedTasksResponse} from "../models/warehouse/DelegatedTasksResponse";
import {UserName} from "../models/manage-users/UserName";
import {UpdateAssignedUserRequest} from "../models/trade/UpdateAssignedUserRequest";

const PRODUCTION_API = 'http://localhost:8080/production/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class ProductionService {

  constructor(private http: HttpClient) { }

  loadProductionTasks(status: string, pageIndex: number, pageSize: number): Observable<DelegatedTasksResponse>{
    return this.http.get<DelegatedTasksResponse>(PRODUCTION_API + `production-tasks/${status}?page=${pageIndex}&size=${pageSize}`);
  }

  markProductionAsInProgress(ids: number[]): Observable<any> {
    return this.http.post(PRODUCTION_API + 'productions/mark-in-progress', JSON.stringify(ids), httpOptions);
  }

  markProductionAsDone(ids: number[]): Observable<any> {
    return this.http.post(PRODUCTION_API + 'productions/mark-done', JSON.stringify(ids), httpOptions);
  }

  delegateInternalAcceptance(ids: number[]): Observable<any> {
    return this.http.post(PRODUCTION_API + 'internal-acceptance', JSON.stringify(ids), httpOptions);
  }

  delegateInternalRelease(ids: number[]): Observable<any> {
    return this.http.post(PRODUCTION_API + 'internal-release', JSON.stringify(ids), httpOptions);
  }

  loadUsers(): Observable<UserName[]> {
    return this.http.get<UserName[]>(PRODUCTION_API + `users`);
  }

  updateAssignedUsers(updateAssignedUserRequest: UpdateAssignedUserRequest): Observable<any> {
    return this.http.put(PRODUCTION_API + 'assigned-user', JSON.stringify(updateAssignedUserRequest), httpOptions);
  }
}
