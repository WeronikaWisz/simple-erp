import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {SuppliesResponse} from "../models/warehouse/SuppliesResponse";
import {UpdateSuppliesRequest} from "../models/warehouse/UpdateSuppliesRequest";
import {PurchaseTaskRequest} from "../models/warehouse/PurchaseTaskRequest";
import {DelegatedTasksResponse} from "../models/warehouse/DelegatedTasksResponse";
import {ReleasesAcceptancesResponse} from "../models/warehouse/ReleasesAcceptancesResponse";
import {UpdateAssignedUserRequest} from "../models/trade/UpdateAssignedUserRequest";
import {UserName} from "../models/manage-users/UserName";
import {ProductCode} from "../models/products/ProductCode";
import {UpdateOrderRequest} from "../models/trade/UpdateOrderRequest";
import {ReleaseDetails} from "../models/warehouse/ReleaseDetails";

const SUPPLIES_API = 'http://localhost:8080/warehouse/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class WarehouseService {

  constructor(private http: HttpClient) { }

  loadSupplies(pageIndex: number, pageSize: number): Observable<SuppliesResponse> {
    return this.http.get<SuppliesResponse>(SUPPLIES_API + `supplies?page=${pageIndex}&size=${pageSize}`);
  }

  updateSupplies(updateSuppliesRequest: UpdateSuppliesRequest): Observable<any> {
    return this.http.put(SUPPLIES_API + 'supplies', JSON.stringify(updateSuppliesRequest), httpOptions);
  }

  delegatePurchaseTask(purchaseTaskRequest: PurchaseTaskRequest): Observable<any> {
    return this.http.post(SUPPLIES_API + 'delegate-purchase', JSON.stringify(purchaseTaskRequest), httpOptions);
  }

  loadDelegatedTasks(type: string, pageIndex: number, pageSize: number): Observable<DelegatedTasksResponse>{
    return this.http.get<DelegatedTasksResponse>(SUPPLIES_API + `delegated-tasks/${type}?page=${pageIndex}&size=${pageSize}`);
  }

  deleteTask(id: number, type: string): Observable<any> {
    return this.http.delete(SUPPLIES_API + 'task/' + type + "/" + id)
  }

  updatePurchaseTask(purchaseTaskRequest: PurchaseTaskRequest): Observable<any> {
    return this.http.put(SUPPLIES_API + 'purchase-task', JSON.stringify(purchaseTaskRequest), httpOptions);
  }

  loadReleases(status: string, direction: string, pageIndex: number, pageSize: number): Observable<ReleasesAcceptancesResponse>{
    console.log(direction)
    if(direction != null) {
      return this.http.get<ReleasesAcceptancesResponse>(SUPPLIES_API + `releases/${status}/${direction}?page=${pageIndex}&size=${pageSize}`);
    } else {
      return this.http.get<ReleasesAcceptancesResponse>(SUPPLIES_API + `releases/${status}?page=${pageIndex}&size=${pageSize}`);
    }
  }

  loadUsers(): Observable<UserName[]> {
    return this.http.get<UserName[]>(SUPPLIES_API + `users`);
  }

  updateAssignedUsers(updateAssignedUserRequest: UpdateAssignedUserRequest): Observable<any> {
    return this.http.put(SUPPLIES_API + 'assigned-user', JSON.stringify(updateAssignedUserRequest), httpOptions);
  }

  markReleaseAsInProgress(ids: number[]): Observable<any> {
    return this.http.post(SUPPLIES_API+ 'releases/mark-in-progress', JSON.stringify(ids), httpOptions);
  }

  markReleaseAsDone(ids: number[]): Observable<any> {
    return this.http.post(SUPPLIES_API + 'releases/mark-done', JSON.stringify(ids), httpOptions);
  }

  getRelease(id: number): Observable<ReleaseDetails> {
    return this.http.get<ReleaseDetails>(SUPPLIES_API + 'release/' + id, httpOptions);
  }

  loadProductList(): Observable<ProductCode[]> {
    return this.http.get<ProductCode[]>(SUPPLIES_API + 'products');
  }

  loadAcceptances(status: string, direction: string, pageIndex: number, pageSize: number): Observable<ReleasesAcceptancesResponse>{
    console.log(direction)
    if(direction != null) {
      return this.http.get<ReleasesAcceptancesResponse>(SUPPLIES_API + `acceptances/${status}/${direction}?page=${pageIndex}&size=${pageSize}`);
    } else {
      return this.http.get<ReleasesAcceptancesResponse>(SUPPLIES_API + `acceptances/${status}?page=${pageIndex}&size=${pageSize}`);
    }
  }

  markAcceptanceAsInProgress(ids: number[]): Observable<any> {
    return this.http.post(SUPPLIES_API+ 'acceptances/mark-in-progress', JSON.stringify(ids), httpOptions);
  }

  markAcceptanceAsDone(ids: number[]): Observable<any> {
    return this.http.post(SUPPLIES_API + 'acceptances/mark-done', JSON.stringify(ids), httpOptions);
  }

  // getAcceptance(id: number): Observable<ReleaseDetails> {
  //   return this.http.get<ReleaseDetails>(SUPPLIES_API + 'release/' + id, httpOptions);
  // }
}
