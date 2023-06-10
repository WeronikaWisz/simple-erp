import { LayoutModule } from '@angular/cdk/layout';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';

import { MainNavComponent } from './main-nav.component';
import {TranslateModule} from "@ngx-translate/core";
import {RouterTestingModule} from "@angular/router/testing";
import {MatMenuModule} from "@angular/material/menu";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {TokenStorageService} from "../services/token-storage.service";

describe('MainNavComponent', () => {
  let component: MainNavComponent;
  let fixture: ComponentFixture<MainNavComponent>;

  let testUser = {
    username: "username",
    roles: ['ROLE_ADMIN']
  }

  let testTokenStorageService = jasmine.createSpyObj(['getUser', 'getToken', 'saveToken', 'saveUser'])
  testTokenStorageService.getToken.and.returnValue('sometoken')
  testTokenStorageService.getUser.and.returnValue(testUser)

  let testMatDialog = jasmine.createSpyObj(['open'])

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MainNavComponent ],
      imports: [
        NoopAnimationsModule,
        LayoutModule,
        MatButtonModule,
        MatIconModule,
        MatListModule,
        MatSidenavModule,
        MatToolbarModule,
        TranslateModule.forRoot(),
        RouterTestingModule,
        MatMenuModule,
        MatDialogModule
      ],
      providers: [
        { provide: TokenStorageService, useValue: testTokenStorageService },
        { provide: MatDialog, useValue: testMatDialog}
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MainNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getToken on tokenStorageService', () => {
    expect(testTokenStorageService.getToken).toHaveBeenCalled();
  });

  it('should call getUser on tokenStorageService', () => {
    expect(testTokenStorageService.getUser).toHaveBeenCalled();
  });

});
