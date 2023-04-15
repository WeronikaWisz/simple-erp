import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AcceptanceInfoDialogComponent } from './acceptance-info-dialog.component';

describe('AcceptanceInfoDialogComponent', () => {
  let component: AcceptanceInfoDialogComponent;
  let fixture: ComponentFixture<AcceptanceInfoDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AcceptanceInfoDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AcceptanceInfoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
