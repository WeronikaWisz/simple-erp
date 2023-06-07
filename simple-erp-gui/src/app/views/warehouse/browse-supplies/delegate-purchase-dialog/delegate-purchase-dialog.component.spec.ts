import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DelegatePurchaseDialogComponent} from './delegate-purchase-dialog.component';
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
import {DelegatedTaskData} from "../../../../models/warehouse/DelegatedTaskData";
import {EUnit} from "../../../../enums/EUnit";
import {EType} from "../../../../enums/EType";

describe('DelegatePurchaseDialogComponent', () => {
  let component: DelegatePurchaseDialogComponent;
  let fixture: ComponentFixture<DelegatePurchaseDialogComponent>;

  let data: DelegatedTaskData = {
    code: "",
    name: "",
    quantity: "",
    stockLevelId: 0,
    stockQuantity: "",
    taskId: 0,
    taskType: EType.BOUGHT,
    unit: EUnit.PIECES
  }

  const dialogMock = {
    close: () => { }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DelegatePurchaseDialogComponent ],
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

    fixture = TestBed.createComponent(DelegatePurchaseDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
