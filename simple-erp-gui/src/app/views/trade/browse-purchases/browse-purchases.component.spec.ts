import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowsePurchasesComponent } from './browse-purchases.component';

describe('BrowsePurchasesComponent', () => {
  let component: BrowsePurchasesComponent;
  let fixture: ComponentFixture<BrowsePurchasesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowsePurchasesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowsePurchasesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
