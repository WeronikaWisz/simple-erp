import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductionInfoDialogComponent } from './production-info-dialog.component';

describe('ProductionInfoDialogComponent', () => {
  let component: ProductionInfoDialogComponent;
  let fixture: ComponentFixture<ProductionInfoDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductionInfoDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductionInfoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
