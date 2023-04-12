import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeUserReleaseDialogComponent } from './change-user-release-dialog.component';

describe('ChangeUserReleaseDialogComponent', () => {
  let component: ChangeUserReleaseDialogComponent;
  let fixture: ComponentFixture<ChangeUserReleaseDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChangeUserReleaseDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeUserReleaseDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
