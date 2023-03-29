import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {SuppliesResponse} from "../models/warehouse/SuppliesResponse";

const SUPPLIES_API = 'http://localhost:8080/supplies/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class SuppliesService {

  constructor(private http: HttpClient) { }

  loadSupplies(pageIndex: number, pageSize: number): Observable<SuppliesResponse> {
    return this.http.get<SuppliesResponse>(SUPPLIES_API + `supplies?page=${pageIndex}&size=${pageSize}`);
  }
}
