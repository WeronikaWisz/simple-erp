import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {ContractorsResponse} from "../models/products/ContractorsResponse";
import {ForecastingActive} from "../models/forecasting/ForecastingActive";

const FORECASTING_API = 'http://localhost:8080/forecasting/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class ForecastingService {

  constructor(private http: HttpClient) { }

  train(file: File) {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<ArrayBuffer>(FORECASTING_API + 'training', formData)
  }

  checkForecastingState(): Observable<ForecastingActive> {
    return this.http.get<ForecastingActive>(FORECASTING_API + 'active');
  }
}
