import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeProductionUserDialogComponent } from './change-production-user-dialog.component';

describe('ChangeProductionUserDialogComponent', () => {
  let component: ChangeProductionUserDialogComponent;
  let fixture: ComponentFixture<ChangeProductionUserDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChangeProductionUserDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeProductionUserDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
