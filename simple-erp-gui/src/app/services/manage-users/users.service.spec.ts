import { TestBed } from '@angular/core/testing';

import { UsersService } from './users.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {ChangePassword} from "../../models/manage-users/ChangePassword";


describe('UsersService', () => {
  let service: UsersService;
  let httpMock: HttpTestingController;

  let testChangePassword: ChangePassword = {
    newPassword: "newPassword",
    oldPassword: "oldPassword"
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(UsersService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('changePassword should PUT on user/password', (done) => {
    service.changePassword(testChangePassword).subscribe(res => done())
    const successRequest = httpMock.expectOne('http://localhost:8080/users/user/password');
    expect(successRequest.request.method).toEqual('PUT');
    successRequest.flush(null);
    httpMock.verify();
  });

  it('loadAssignedUser should GET on assigned-user/:userId', (done) => {
    service.loadAssignedUser(1).subscribe(res => done())
    const successRequest = httpMock.expectOne('http://localhost:8080/users/assigned-user/1');
    expect(successRequest.request.method).toEqual('GET');
    successRequest.flush(null);
    httpMock.verify();
  });
});
