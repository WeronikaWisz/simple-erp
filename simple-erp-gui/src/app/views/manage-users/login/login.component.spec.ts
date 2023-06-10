import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {TokenStorageService} from "../../../services/token-storage.service";
import {AuthService} from "../../../services/manage-users/auth.service";
import {LoginRequest} from "../../../models/auth/LoginRequest";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MaterialModule} from "../../../helpers/MaterialModule";

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  let testUser = {
    username: "username",
    roles: ['ROLE_ADMIN']
  }

  let testTokenStorageService = jasmine.createSpyObj(['getUser', 'getToken', 'saveToken', 'saveUser'])
  testTokenStorageService.getToken.and.returnValue('sometoken')
  testTokenStorageService.getUser.and.returnValue(testUser)

  let testAuthService = jasmine.createSpyObj(['login'])

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule,
        TranslateModule.forRoot(),
        BrowserModule,
        BrowserAnimationsModule,
        MaterialModule
      ],
      providers: [
        { provide: TokenStorageService, useValue: testTokenStorageService },
        { provide: AuthService, useValue: testAuthService }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getToken on tokenStorageService', () => {
    expect(testTokenStorageService.getToken).toHaveBeenCalled();
  });

  it('should call getUser on tokenStorageService', () => {
    expect(testTokenStorageService.getUser).toHaveBeenCalled();
  });

});

describe('LoginComponent integration test with AuthService', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let service: AuthService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule,
        TranslateModule.forRoot()
      ]
    })
      .compileComponents();
    service = TestBed.inject(AuthService)
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('login user', () => {

    it('loginButton should call service login', () => {
      component.form.controls['username'].setValue('username')
      component.form.controls['password'].setValue('passwordpassword')

      fixture.detectChanges()

      let request : LoginRequest = {
        "username": 'username',
        "password": 'passwordpassword'
      }

      let spy = spyOn(service, 'login').withArgs(request).and.callThrough()
      const btn = fixture.debugElement.nativeElement.querySelector('#submit-login-button')
      btn.click()
      fixture.detectChanges()
      expect(spy).toHaveBeenCalledWith(request)
    });
  });


});
