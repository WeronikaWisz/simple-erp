import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrainingEvaluationComponent } from './training-evaluation.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MatDialogModule} from "@angular/material/dialog";
import {TokenStorageService} from "../../../services/token-storage.service";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatSelectModule} from "@angular/material/select";
import {MatOptionModule} from "@angular/material/core";
import {MatInputModule} from "@angular/material/input";
import {CUSTOM_ELEMENTS_SCHEMA} from "@angular/core";
import {MatCardModule} from "@angular/material/card";
import {MaterialModule} from "../../../helpers/MaterialModule";

describe('TrainingEvaluationComponent', () => {
  let component: TrainingEvaluationComponent;
  let fixture: ComponentFixture<TrainingEvaluationComponent>;

  let testUser = {
    username: "username",
    roles: ['ROLE_ADMIN']
  }

  let testTokenStorageService = jasmine.createSpyObj(['getUser', 'getToken', 'saveToken', 'saveUser'])
  testTokenStorageService.getToken.and.returnValue('sometoken')
  testTokenStorageService.getUser.and.returnValue(testUser)

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TrainingEvaluationComponent ],
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
        { provide: TokenStorageService, useValue: testTokenStorageService }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrainingEvaluationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
