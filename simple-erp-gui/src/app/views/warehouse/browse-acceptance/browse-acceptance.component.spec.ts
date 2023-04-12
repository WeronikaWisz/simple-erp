import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseAcceptanceComponent } from './browse-acceptance.component';

describe('BrowseAcceptanceComponent', () => {
  let component: BrowseAcceptanceComponent;
  let fixture: ComponentFixture<BrowseAcceptanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseAcceptanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseAcceptanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
