import {ComponentFixture, TestBed} from '@angular/core/testing';

import { ChangePasswordDialogComponent } from './change-password-dialog.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {of, throwError} from "rxjs";
import {UsersService} from "../../../../services/manage-users/users.service";
import Swal from "sweetalert2";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MaterialModule} from "../../../../helpers/MaterialModule";

describe('ChangePasswordDialogComponent', () => {
  let component: ChangePasswordDialogComponent;
  let fixture: ComponentFixture<ChangePasswordDialogComponent>;

  const dialogMock = {
    close: () => { }
  }

  let testUserService = jasmine.createSpyObj(['changePassword'])

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChangePasswordDialogComponent ],
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
        { provide: MatDialogRef, useValue: dialogMock },
        { provide: UsersService, useValue: testUserService }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangePasswordDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('onNoClick should close dialog', () => {
    let spy = spyOn(component.dialogRef, 'close').and.callThrough();
    component.onNoClick();
    expect(spy).toHaveBeenCalled();
  });

  it('changePassword should call userService changePassword', () => {
    testUserService.changePassword.and.returnValue(of("data"))
    spyOn(Swal,"fire").and.stub();
    component.changePassword();
    expect(testUserService.changePassword).toHaveBeenCalled();
  });

  it('changePassword should show error while error from usersService', () => {
    testUserService.changePassword.and.returnValue(throwError({error: {status: 404, message: 'error'}}))
    let spy = spyOn(Swal,"fire").and.stub();
    component.changePassword()
    expect(spy).toHaveBeenCalled()
  });

});
