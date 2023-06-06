import { TestBed } from '@angular/core/testing';

import { ProductionService } from './production.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('ProductionService', () => {
  let service: ProductionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(ProductionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
