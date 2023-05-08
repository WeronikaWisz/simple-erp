import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Pipe({
  name: 'localizedDate',
  pure: false,
})
export class LocalizedDatePipe implements PipeTransform {
  constructor(private translateService: TranslateService) {}

  transform(value: Date | string, format = 'mediumDate'): string {
    const datePipe = new DatePipe(this.translateService.currentLang || 'en');
    return datePipe.transform(value, format)!;
  }
}
