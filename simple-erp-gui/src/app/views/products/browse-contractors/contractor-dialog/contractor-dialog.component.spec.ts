import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractorDialogComponent } from './contractor-dialog.component';
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
import {ProductProductionDialogData} from "../../../../models/production/ProductProductionDialogData";
import {ContractorDialogInfo} from "../../../../models/products/ContractorDialogInfo";
import {MaterialModule} from "../../../../helpers/MaterialModule";

describe('ContractorDialogComponent', () => {
  let component: ContractorDialogComponent;
  let fixture: ComponentFixture<ContractorDialogComponent>;

  let data: ContractorDialogInfo = {
    id: 1, isFromTrade: false
  }

  const dialogMock = {
    close: () => { }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContractorDialogComponent ],
      imports: [
        FormsModule,
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
        { provide: MAT_DIALOG_DATA, useValue: data }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContractorDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
