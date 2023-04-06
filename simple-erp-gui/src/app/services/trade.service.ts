import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {ProductCode} from "../models/products/ProductCode";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AddProductRequest} from "../models/products/AddProductRequest";
import {AddOrderRequest} from "../models/trade/AddOrderRequest";

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

}
