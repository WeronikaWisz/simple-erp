import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {ProductCode} from "../models/products/ProductCode";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AddProductRequest} from "../models/products/AddProductRequest";
import {AddOrderRequest} from "../models/trade/AddOrderRequest";
import {DelegatedTasksResponse} from "../models/warehouse/DelegatedTasksResponse";
import {OrdersResponse} from "../models/trade/OrdersResponse";
import {OrderListItem} from "../models/trade/OrderListItem";
import {UpdateAssignedUserRequest} from "../models/trade/UpdateAssignedUserRequest";
import {UserName} from "../models/manage-users/UserName";
import {UpdateDefaultUserRequest} from "../models/manage-users/UpdateDefaultUserRequest";
import {EType} from "../enums/EType";
import {ProductListItem} from "../models/products/ProductListItem";
import {UpdateProductRequest} from "../models/products/UpdateProductRequest";
import {UpdateOrderRequest} from "../models/trade/UpdateOrderRequest";

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
}
