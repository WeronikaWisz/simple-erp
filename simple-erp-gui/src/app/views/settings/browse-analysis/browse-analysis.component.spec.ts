import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseAnalysisComponent } from './browse-analysis.component';

describe('BrowseAnalysisComponent', () => {
  let component: BrowseAnalysisComponent;
  let fixture: ComponentFixture<BrowseAnalysisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseAnalysisComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseAnalysisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
