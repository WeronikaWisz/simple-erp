import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseDelegatedWarehouseTaskComponent } from './browse-delegated-warehouse-task.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MatDialogModule} from "@angular/material/dialog";
import {TokenStorageService} from "../../../services/token-storage.service";

describe('BrowseDelegatedWarehouseTaskComponent', () => {
  let component: BrowseDelegatedWarehouseTaskComponent;
  let fixture: ComponentFixture<BrowseDelegatedWarehouseTaskComponent>;

  let testUser = {
    username: "username",
    roles: ['ROLE_ADMIN']
  }

  let testTokenStorageService = jasmine.createSpyObj(['getUser', 'getToken', 'saveToken', 'saveUser'])
  testTokenStorageService.getToken.and.returnValue('sometoken')
  testTokenStorageService.getUser.and.returnValue(testUser)

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseDelegatedWarehouseTaskComponent ],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule,
        TranslateModule.forRoot(),
        MatDialogModule
      ],
      providers: [
        { provide: TokenStorageService, useValue: testTokenStorageService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseDelegatedWarehouseTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
