import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseProductionProductsComponent } from './browse-production-products.component';

describe('BrowseProductionProductsComponent', () => {
  let component: BrowseProductionProductsComponent;
  let fixture: ComponentFixture<BrowseProductionProductsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseProductionProductsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseProductionProductsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
