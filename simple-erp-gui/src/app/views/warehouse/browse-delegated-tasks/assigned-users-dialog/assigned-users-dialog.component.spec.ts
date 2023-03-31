import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignedUsersDialogComponent } from './assigned-users-dialog.component';

describe('AssignedUsersDialogComponent', () => {
  let component: AssignedUsersDialogComponent;
  let fixture: ComponentFixture<AssignedUsersDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssignedUsersDialogComponent ]
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
