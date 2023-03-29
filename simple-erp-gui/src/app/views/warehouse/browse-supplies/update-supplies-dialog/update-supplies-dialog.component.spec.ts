import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateSuppliesDialogComponent } from './update-supplies-dialog.component';

describe('UpdateSuppliesDialogComponent', () => {
  let component: UpdateSuppliesDialogComponent;
  let fixture: ComponentFixture<UpdateSuppliesDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateSuppliesDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateSuppliesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
