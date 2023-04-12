import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseReleaseComponent } from './browse-release.component';

describe('BrowseReleaseComponent', () => {
  let component: BrowseReleaseComponent;
  let fixture: ComponentFixture<BrowseReleaseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseReleaseComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseReleaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
