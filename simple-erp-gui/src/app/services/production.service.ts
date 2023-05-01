import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {DelegatedTasksResponse} from "../models/warehouse/DelegatedTasksResponse";
import {UserName} from "../models/manage-users/UserName";
import {UpdateAssignedUserRequest} from "../models/trade/UpdateAssignedUserRequest";
import {ReleasesAcceptancesResponse} from "../models/warehouse/ReleasesAcceptancesResponse";
import {ReleaseDetails} from "../models/warehouse/ReleaseDetails";
import {AcceptanceDetails} from "../models/warehouse/AcceptanceDetails";
import {ProductCode} from "../models/products/ProductCode";
import {ProductProductionInfo} from "../models/production/ProductProductionInfo";
import {ProductionProductResponse} from "../models/production/ProductionProductResponse";

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

  loadDelegatedTasks(task: string, pageIndex: number, pageSize: number): Observable<ReleasesAcceptancesResponse>{
    return this.http.get<ReleasesAcceptancesResponse>(PRODUCTION_API + `delegated-tasks/${task}?page=${pageIndex}&size=${pageSize}`);
  }

  getRelease(id: number): Observable<ReleaseDetails> {
    return this.http.get<ReleaseDetails>(PRODUCTION_API + 'release/' + id, httpOptions);
  }

  getAcceptance(id: number): Observable<AcceptanceDetails> {
    return this.http.get<AcceptanceDetails>(PRODUCTION_API + 'acceptance/' + id, httpOptions);
  }

  loadProductList(): Observable<ProductCode[]> {
    return this.http.get<ProductCode[]>(PRODUCTION_API + 'products');
  }

  getProductionInfo(id: number): Observable<ProductProductionInfo> {
    return this.http.get<ProductProductionInfo>(PRODUCTION_API + 'production-info/' + id, httpOptions);
  }

  loadProductionProducts(pageIndex: number, pageSize: number): Observable<ProductionProductResponse> {
    return this.http.get<ProductionProductResponse>(PRODUCTION_API + `production-products?page=${pageIndex}&size=${pageSize}`);
  }

  getProductInfo(id: number): Observable<ProductProductionInfo> {
    return this.http.get<ProductProductionInfo>(PRODUCTION_API + 'product-info/' + id, httpOptions);
  }
}
