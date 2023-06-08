import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseAnalysisComponent } from './browse-analysis.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MatDialogModule} from "@angular/material/dialog";
import {TokenStorageService} from "../../../services/token-storage.service";
import {LocalizedDatePipe} from "../../../helpers/localized-date.pipe";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MaterialModule} from "../../../helpers/MaterialModule";

describe('BrowseAnalysisComponent', () => {
  let component: BrowseAnalysisComponent;
  let fixture: ComponentFixture<BrowseAnalysisComponent>;

  let testUser = {
    username: "username",
    roles: ['ROLE_ADMIN']
  }

  let testTokenStorageService = jasmine.createSpyObj(['getUser', 'getToken', 'saveToken', 'saveUser'])
  testTokenStorageService.getToken.and.returnValue('sometoken')
  testTokenStorageService.getUser.and.returnValue(testUser)

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseAnalysisComponent, LocalizedDatePipe ],
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
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseAnalysisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
