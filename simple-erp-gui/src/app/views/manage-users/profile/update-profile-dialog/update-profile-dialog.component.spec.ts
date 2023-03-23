import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateProfileDialogComponent } from './update-profile-dialog.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";

describe('UpdateProfileDialogComponent', () => {
  let component: UpdateProfileDialogComponent;
  let fixture: ComponentFixture<UpdateProfileDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateProfileDialogComponent ],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule,
        TranslateModule.forRoot(), MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {}
        },
        { provide: MAT_DIALOG_DATA, useValue: {} }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateProfileDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
