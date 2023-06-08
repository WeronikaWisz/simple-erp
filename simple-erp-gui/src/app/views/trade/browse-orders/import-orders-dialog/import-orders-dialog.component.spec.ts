import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportOrdersDialogComponent } from './import-orders-dialog.component';
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
import {MaterialModule} from "../../../../helpers/MaterialModule";

describe('ImportOrdersDialogComponent', () => {
  let component: ImportOrdersDialogComponent;
  let fixture: ComponentFixture<ImportOrdersDialogComponent>;

  const dialogMock = {
    close: () => { }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImportOrdersDialogComponent ],
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
        { provide: MatDialogRef, useValue: dialogMock }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImportOrdersDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
