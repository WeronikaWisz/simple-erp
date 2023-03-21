import { Component } from '@angular/core';
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  title = 'Simple ERP';

  constructor(public translate: TranslateService) { }

  ngOnInit(): void {
    this.translate.addLangs(['pl', 'en']);
    this.translate.setDefaultLang('pl');

    const browserLang = this.translate.getBrowserLang();
    this.translate.use(browserLang?.match(/pl|en/) ? browserLang : 'pl');

    console.log(browserLang);
  }
}
