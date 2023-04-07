import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {CustomerData} from "../models/trade/CustomerData";

const CUSTOMERS_API = 'http://localhost:8080/customers/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class CustomersService {

  constructor(private http: HttpClient) { }

  getCustomerData(id: number): Observable<CustomerData>{
    return this.http.get<CustomerData>(CUSTOMERS_API + 'customer/' + id);
  }
}
