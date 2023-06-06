import { TestBed } from '@angular/core/testing';

import { ManageUsersService } from './manage-users.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('ManageUsersService', () => {
  let service: ManageUsersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(ManageUsersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
