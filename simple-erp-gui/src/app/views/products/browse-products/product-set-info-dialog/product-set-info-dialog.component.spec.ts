import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductSetInfoDialogComponent } from './product-set-info-dialog.component';

describe('ProductSetInfoDialogComponent', () => {
  let component: ProductSetInfoDialogComponent;
  let fixture: ComponentFixture<ProductSetInfoDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductSetInfoDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductSetInfoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
