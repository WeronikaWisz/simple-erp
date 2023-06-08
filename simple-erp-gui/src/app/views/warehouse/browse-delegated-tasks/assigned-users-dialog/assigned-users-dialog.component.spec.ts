import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignedUsersDialogComponent } from './assigned-users-dialog.component';
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

describe('AssignedUsersDialogComponent', () => {
  let component: AssignedUsersDialogComponent;
  let fixture: ComponentFixture<AssignedUsersDialogComponent>;

  const dialogMock = {
    close: () => { }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssignedUsersDialogComponent ],
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
        { provide: MAT_DIALOG_DATA, useValue: 1 }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssignedUsersDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
