import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PurchaseOrderNumberDialogComponent } from './purchase-order-number-dialog.component';

describe('PurchaseOrderNumberDialogComponent', () => {
  let component: PurchaseOrderNumberDialogComponent;
  let fixture: ComponentFixture<PurchaseOrderNumberDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PurchaseOrderNumberDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PurchaseOrderNumberDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
