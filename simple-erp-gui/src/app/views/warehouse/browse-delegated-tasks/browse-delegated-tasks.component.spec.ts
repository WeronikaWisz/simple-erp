import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseDelegatedTasksComponent } from './browse-delegated-tasks.component';

describe('BrowseDelegatedTasksComponent', () => {
  let component: BrowseDelegatedTasksComponent;
  let fixture: ComponentFixture<BrowseDelegatedTasksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseDelegatedTasksComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseDelegatedTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
