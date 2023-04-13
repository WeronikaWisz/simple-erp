import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseDelegatedWarehouseTaskComponent } from './browse-delegated-warehouse-task.component';

describe('BrowseDelegatedWarehouseTaskComponent', () => {
  let component: BrowseDelegatedWarehouseTaskComponent;
  let fixture: ComponentFixture<BrowseDelegatedWarehouseTaskComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseDelegatedWarehouseTaskComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseDelegatedWarehouseTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
