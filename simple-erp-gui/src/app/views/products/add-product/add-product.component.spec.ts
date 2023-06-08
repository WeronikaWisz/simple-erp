import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddProductComponent } from './add-product.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatSelectModule} from "@angular/material/select";
import {MatOptionModule} from "@angular/material/core";
import {MatInputModule} from "@angular/material/input";
import {TokenStorageService} from "../../../services/token-storage.service";
import {CUSTOM_ELEMENTS_SCHEMA} from "@angular/core";
import {MaterialModule} from "../../../helpers/MaterialModule";

describe('AddProductComponent', () => {
  let component: AddProductComponent;
  let fixture: ComponentFixture<AddProductComponent>;

  let testUser = {
    username: "username",
    roles: ['ROLE_ADMIN']
  }

  let testTokenStorageService = jasmine.createSpyObj(['getUser', 'getToken', 'saveToken', 'saveUser'])
  testTokenStorageService.getToken.and.returnValue('sometoken')
  testTokenStorageService.getUser.and.returnValue(testUser)

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddProductComponent ],
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
        { provide: TokenStorageService, useValue: testTokenStorageService }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
