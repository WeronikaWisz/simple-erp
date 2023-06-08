import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UpdateSuppliesDialogComponent} from './update-supplies-dialog.component';
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
import {EUnit} from "../../../../enums/EUnit";
import {UpdateSuppliesData} from "../../../../models/warehouse/UpdateSuppliesData";

describe('UpdateSuppliesDialogComponent', () => {
  let component: UpdateSuppliesDialogComponent;
  let fixture: ComponentFixture<UpdateSuppliesDialogComponent>;

  let data: UpdateSuppliesData = {
    code: "", days: 0, id: 0, minQuantity: "", name: "", quantity: "", unit: EUnit.PIECES
  }

  const dialogMock = {
    close: () => { }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateSuppliesDialogComponent ],
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

    fixture = TestBed.createComponent(UpdateSuppliesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
      expect(component).toBeTruthy();
  });
});
