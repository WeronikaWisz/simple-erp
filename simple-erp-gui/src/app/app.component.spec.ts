import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import {TranslateModule, TranslateService} from "@ngx-translate/core";

describe('AppComponent', () => {
  let testTranslateService = jasmine.createSpyObj(['addLangs', 'setDefaultLang', 'getBrowserLang', 'use'])

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule, TranslateModule.forRoot(),
      ],
      declarations: [
        AppComponent
      ],
      providers: [
        { provide : TranslateService, useValue: testTranslateService }
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'Simple ERP'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('Simple ERP');
  });

  it(`should add translate languages`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    app.ngOnInit()
    expect(testTranslateService.addLangs).toHaveBeenCalledWith(['pl', 'en'])
  });

  it(`should set default language`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    app.ngOnInit()
    expect(testTranslateService.setDefaultLang).toHaveBeenCalledWith('pl')
  });

  it(`should get browser lang`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    app.ngOnInit()
    expect(testTranslateService.getBrowserLang).toHaveBeenCalled()
  });
});
