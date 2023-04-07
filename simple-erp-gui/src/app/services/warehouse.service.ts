import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {SuppliesResponse} from "../models/warehouse/SuppliesResponse";
import {UpdateSuppliesRequest} from "../models/warehouse/UpdateSuppliesRequest";
import {PurchaseTaskRequest} from "../models/warehouse/PurchaseTaskRequest";
import {DelegatedTasksResponse} from "../models/warehouse/DelegatedTasksResponse";
import {EType} from "../enums/EType";
import {AssignedUser} from "../models/warehouse/AssignedUser";

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

}
