import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {ProductCode} from "../models/products/ProductCode";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AddOrderRequest} from "../models/trade/AddOrderRequest";
import {DelegatedTasksResponse} from "../models/warehouse/DelegatedTasksResponse";
import {OrdersResponse} from "../models/trade/OrdersResponse";
import {UpdateAssignedUserRequest} from "../models/trade/UpdateAssignedUserRequest";
import {UserName} from "../models/manage-users/UserName";
import {UpdateOrderRequest} from "../models/trade/UpdateOrderRequest";
import {DelegateExternalAcceptance} from "../models/trade/DelegateExternalAcceptance";
import {ReleasesAcceptancesResponse} from "../models/warehouse/ReleasesAcceptancesResponse";
import {ReleaseDetails} from "../models/warehouse/ReleaseDetails";
import {AcceptanceDetails} from "../models/warehouse/AcceptanceDetails";
import {UpdateContractorRequest} from "../models/products/UpdateContractorRequest";

const TRADE_API = 'http://localhost:8080/trade/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class TradeService {

  constructor(private http: HttpClient) { }

  loadProductList(): Observable<ProductCode[]> {
    return this.http.get<ProductCode[]>(TRADE_API + 'products');
  }

  addOrder(addOrderRequest: AddOrderRequest): Observable<any> {
    console.log(JSON.stringify(addOrderRequest));
    return this.http.post(TRADE_API + 'order', JSON.stringify(addOrderRequest), httpOptions);
  }

  deleteOrder(id: number): Observable<any> {
    return this.http.delete(TRADE_API + 'order/' + id)
  }

  loadOrders(status: string, pageIndex: number, pageSize: number): Observable<OrdersResponse>{
    return this.http.get<OrdersResponse>(TRADE_API + `orders/${status}?page=${pageIndex}&size=${pageSize}`);
  }

  loadUsers(): Observable<UserName[]> {
    return this.http.get<UserName[]>(TRADE_API + `users`);
  }

  updateAssignedUsers(updateAssignedUserRequest: UpdateAssignedUserRequest): Observable<any> {
    return this.http.put(TRADE_API + 'assigned-user', JSON.stringify(updateAssignedUserRequest), httpOptions);
  }

  getOrder(id: number): Observable<UpdateOrderRequest> {
    return this.http.get<UpdateOrderRequest>(TRADE_API + 'order/' + id, httpOptions);
  }

  updateOrder(updateOrderRequest: UpdateOrderRequest): Observable<any> {
    return this.http.put(TRADE_API + 'order', JSON.stringify(updateOrderRequest), httpOptions);
  }

  delegateExternalRelease(ids: number[]): Observable<any> {
    return this.http.post(TRADE_API + 'external-release', JSON.stringify(ids), httpOptions);
  }

  markAsReceived(ids: number[]): Observable<any> {
    return this.http.post(TRADE_API + 'mark-received', JSON.stringify(ids), httpOptions);
  }

  loadPurchaseTasks(status: string, pageIndex: number, pageSize: number): Observable<DelegatedTasksResponse>{
    return this.http.get<DelegatedTasksResponse>(TRADE_API + `purchase-tasks/${status}?page=${pageIndex}&size=${pageSize}`);
  }

  markPurchaseAsInProgress(ids: number[]): Observable<any> {
    return this.http.post(TRADE_API + 'purchases/mark-in-progress', JSON.stringify(ids), httpOptions);
  }

  markPurchaseAsDone(ids: number[]): Observable<any> {
    return this.http.post(TRADE_API + 'purchases/mark-done', JSON.stringify(ids), httpOptions);
  }

  delegateExternalAcceptance(delegateExternalAcceptance: DelegateExternalAcceptance): Observable<any> {
    return this.http.post(TRADE_API + 'external-acceptance', JSON.stringify(delegateExternalAcceptance), httpOptions);
  }

  loadDelegatedTasks(task: string, pageIndex: number, pageSize: number): Observable<ReleasesAcceptancesResponse>{
    return this.http.get<ReleasesAcceptancesResponse>(TRADE_API + `delegated-tasks/${task}?page=${pageIndex}&size=${pageSize}`);
  }

  getRelease(id: number): Observable<ReleaseDetails> {
    return this.http.get<ReleaseDetails>(TRADE_API + 'release/' + id, httpOptions);
  }

  getAcceptance(id: number): Observable<AcceptanceDetails> {
    return this.http.get<AcceptanceDetails>(TRADE_API + 'acceptance/' + id, httpOptions);
  }

  getContractor(id: number): Observable<UpdateContractorRequest>{
    return this.http.get<UpdateContractorRequest>(TRADE_API + 'purchase/contractor/' + id, httpOptions);
  }
}
