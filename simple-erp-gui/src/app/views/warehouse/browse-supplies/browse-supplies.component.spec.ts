import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseSuppliesComponent } from './browse-supplies.component';

describe('BrowseSuppliesComponent', () => {
  let component: BrowseSuppliesComponent;
  let fixture: ComponentFixture<BrowseSuppliesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseSuppliesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseSuppliesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
