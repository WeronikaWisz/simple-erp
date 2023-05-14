import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrainingEvaluationComponent } from './training-evaluation.component';

describe('TrainingEvaluationComponent', () => {
  let component: TrainingEvaluationComponent;
  let fixture: ComponentFixture<TrainingEvaluationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TrainingEvaluationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrainingEvaluationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
