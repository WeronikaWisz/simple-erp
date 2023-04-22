import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseProductionComponent } from './browse-production.component';

describe('BrowseProductionComponent', () => {
  let component: BrowseProductionComponent;
  let fixture: ComponentFixture<BrowseProductionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseProductionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseProductionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
