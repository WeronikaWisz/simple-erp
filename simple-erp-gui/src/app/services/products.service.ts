import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {ProductCode} from "../models/products/ProductCode";
import {AddProductRequest} from "../models/products/AddProductRequest";
import {ProductListItem} from "../models/products/ProductListItem";
import {ProductsResponse} from "../models/products/ProductsResponse";
import {EType} from "../enums/EType";
import {UpdateUserRequest} from "../models/auth/UpdateUserRequest";
import {UpdateProductRequest} from "../models/products/UpdateProductRequest";

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

  loadProducts(pageIndex: number, pageSize: number): Observable<ProductsResponse> {
    return this.http.get<ProductsResponse>(PRODUCTS_API + `products?page=${pageIndex}&size=${pageSize}`);
  }

  deleteProduct(id: number, type: EType): Observable<any> {
    return this.http.delete(PRODUCTS_API + 'product/' + type + "/" + id)
  }

  getProduct(id: number, type: EType): Observable<ProductListItem> {
    return this.http.get<ProductListItem>(PRODUCTS_API + 'product/' + type + "/" + id, httpOptions);
  }

  updateProduct(updateProductRequest: UpdateProductRequest): Observable<any> {
    return this.http.put(PRODUCTS_API + 'product', JSON.stringify(updateProductRequest), httpOptions);
  }
}
