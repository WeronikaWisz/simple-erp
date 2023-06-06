import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {LoginRequest} from "../../models/auth/LoginRequest";

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  let testLoginRequest : LoginRequest = {
    password: "username",
    username: "password"
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('login should POST on signin', (done) => {
    service.login(testLoginRequest).subscribe(res => done())
    const successRequest = httpMock.expectOne('http://localhost:8080/auth/signin');
    expect(successRequest.request.method).toEqual('POST');
    successRequest.flush(null);
    httpMock.verify();
  });

});
