import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {ProductCode} from "../models/products/ProductCode";
import {AddProductRequest} from "../models/products/AddProductRequest";

const PRODUCTS_API = 'http://localhost:8080/products/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  constructor(private http: HttpClient) { }

  loadProductForSetList(): Observable<ProductCode[]> {
    return this.http.get<ProductCode[]>(PRODUCTS_API + 'set-products');
  }

  addProduct(addProductRequest: AddProductRequest): Observable<any> {
    return this.http.post(PRODUCTS_API + 'product', JSON.stringify(addProductRequest), httpOptions);
  }
}
