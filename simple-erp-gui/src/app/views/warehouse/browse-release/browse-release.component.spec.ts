import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseReleaseComponent } from './browse-release.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MatDialogModule} from "@angular/material/dialog";
import {TokenStorageService} from "../../../services/token-storage.service";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatTabsModule} from "@angular/material/tabs";
import {CUSTOM_ELEMENTS_SCHEMA} from "@angular/core";
import {MatIconModule} from "@angular/material/icon";
import {MaterialModule} from "../../../helpers/MaterialModule";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

describe('BrowseReleaseComponent', () => {
  let component: BrowseReleaseComponent;
  let fixture: ComponentFixture<BrowseReleaseComponent>;

  let testUser = {
    username: "username",
    roles: ['ROLE_ADMIN']
  }

  let testTokenStorageService = jasmine.createSpyObj(['getUser', 'getToken', 'saveToken', 'saveUser'])
  testTokenStorageService.getToken.and.returnValue('sometoken')
  testTokenStorageService.getUser.and.returnValue(testUser)

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseReleaseComponent ],
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

    fixture = TestBed.createComponent(BrowseReleaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
