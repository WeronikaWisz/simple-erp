import { TestBed } from '@angular/core/testing';

import { ForecastingService } from './forecasting.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('ForecastingService', () => {
  let service: ForecastingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(ForecastingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
