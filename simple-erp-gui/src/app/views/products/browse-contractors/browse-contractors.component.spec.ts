import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseContractorsComponent } from './browse-contractors.component';

describe('BrowseContractorsComponent', () => {
  let component: BrowseContractorsComponent;
  let fixture: ComponentFixture<BrowseContractorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseContractorsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseContractorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
