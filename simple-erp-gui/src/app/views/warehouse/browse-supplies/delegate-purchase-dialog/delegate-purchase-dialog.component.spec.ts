import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DelegatePurchaseDialogComponent } from './delegate-purchase-dialog.component';

describe('DelegatePurchaseDialogComponent', () => {
  let component: DelegatePurchaseDialogComponent;
  let fixture: ComponentFixture<DelegatePurchaseDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DelegatePurchaseDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DelegatePurchaseDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
