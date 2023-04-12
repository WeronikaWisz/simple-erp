import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReleaseInfoDialogComponent } from './release-info-dialog.component';

describe('ReleaseInfoDialogComponent', () => {
  let component: ReleaseInfoDialogComponent;
  let fixture: ComponentFixture<ReleaseInfoDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReleaseInfoDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReleaseInfoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
