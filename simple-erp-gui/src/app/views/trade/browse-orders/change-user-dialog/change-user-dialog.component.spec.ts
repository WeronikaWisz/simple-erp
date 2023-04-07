import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeUserDialogComponent } from './change-user-dialog.component';

describe('ChangeUserDialogComponent', () => {
  let component: ChangeUserDialogComponent;
  let fixture: ComponentFixture<ChangeUserDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChangeUserDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeUserDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
