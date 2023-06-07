import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PurchaseOrderNumberDialogComponent } from './purchase-order-number-dialog.component';
import {ChangeUserDialogComponent} from "../../browse-orders/change-user-dialog/change-user-dialog.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {TranslateModule} from "@ngx-translate/core";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatSelectModule} from "@angular/material/select";
import {MatOptionModule} from "@angular/material/core";
import {MatInputModule} from "@angular/material/input";
import {CUSTOM_ELEMENTS_SCHEMA} from "@angular/core";
import {TaskNumberType} from "../../../../models/trade/TaskNumberType";
import {ETask} from "../../../../enums/ETask";
import {DelegateExternalAcceptance} from "../../../../models/trade/DelegateExternalAcceptance";

describe('PurchaseOrderNumberDialogComponent', () => {
  let component: PurchaseOrderNumberDialogComponent;
  let fixture: ComponentFixture<PurchaseOrderNumberDialogComponent>;

  let data: DelegateExternalAcceptance = {
    ids: [], orderNumber: ""
  }

  const dialogMock = {
    close: () => { }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PurchaseOrderNumberDialogComponent ],
      imports: [
        FormsModule,
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule,
        MatDialogModule,
        TranslateModule.forRoot(),
        BrowserModule,
        BrowserAnimationsModule,
        MatSelectModule,
        MatOptionModule,
        MatInputModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: dialogMock },
        { provide: MAT_DIALOG_DATA, useValue: data }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PurchaseOrderNumberDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
