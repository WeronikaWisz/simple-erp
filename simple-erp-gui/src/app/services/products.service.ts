import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {ProductCode} from "../models/products/ProductCode";
import {AddProductRequest} from "../models/products/AddProductRequest";
import {ProductListItem} from "../models/products/ProductListItem";
import {ProductsResponse} from "../models/products/ProductsResponse";
import {EType} from "../enums/EType";
import {UpdateProductRequest} from "../models/products/UpdateProductRequest";
import {AddContractorRequest} from "../models/products/AddContractorRequest";
import {ContractorsResponse} from "../models/products/ContractorsResponse";
import {UpdateContractorRequest} from "../models/products/UpdateContractorRequest";
import {ContractorName} from "../models/products/ContractorName";

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

  addContractor(addContractorRequest: AddContractorRequest): Observable<any> {
    return this.http.post(PRODUCTS_API + 'contractor', JSON.stringify(addContractorRequest), httpOptions);
  }

  loadContractors(pageIndex: number, pageSize: number): Observable<ContractorsResponse> {
    return this.http.get<ContractorsResponse>(PRODUCTS_API + `contractors?page=${pageIndex}&size=${pageSize}`);
  }

  deleteContractor(id: number): Observable<any> {
    return this.http.delete(PRODUCTS_API + 'contractor/' + id)
  }

  getContractor(id: number): Observable<UpdateContractorRequest> {
    return this.http.get<UpdateContractorRequest>(PRODUCTS_API + 'contractor/' + id, httpOptions);
  }

  updateContractor(updateContractorRequest: UpdateContractorRequest): Observable<any> {
    return this.http.put(PRODUCTS_API + 'contractor', JSON.stringify(updateContractorRequest), httpOptions);
  }

  loadContractorsNames(): Observable<ContractorName[]> {
    return this.http.get<ContractorName[]>(PRODUCTS_API + `contractors-names`);
  }
}
