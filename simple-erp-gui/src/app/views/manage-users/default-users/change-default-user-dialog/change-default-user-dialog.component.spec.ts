import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeDefaultUserDialogComponent } from './change-default-user-dialog.component';

describe('ChangeDefaultUserDialogComponent', () => {
  let component: ChangeDefaultUserDialogComponent;
  let fixture: ComponentFixture<ChangeDefaultUserDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChangeDefaultUserDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeDefaultUserDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
