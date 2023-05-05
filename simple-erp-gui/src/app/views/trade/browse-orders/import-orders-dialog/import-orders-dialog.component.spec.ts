import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportOrdersDialogComponent } from './import-orders-dialog.component';

describe('ImportOrdersDialogComponent', () => {
  let component: ImportOrdersDialogComponent;
  let fixture: ComponentFixture<ImportOrdersDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImportOrdersDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImportOrdersDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
