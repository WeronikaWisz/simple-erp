import { Component, OnInit } from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {FormBuilder} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import {ForecastingService} from "../../../services/forecasting.service";
import Swal from "sweetalert2";

export interface ExampleElement {
  date: string;
  code: string;
  quantity: string;
}

const ELEMENT_DATA: ExampleElement[] = [
  {date: "2023-01-15", code: "KU-001-01", quantity: "12"},
  {date: "2023-01-15", code: "KU-001-02", quantity: "5"},
  {date: "2023-01-20", code: "KU-001-01", quantity: "7"},
  {date: "2023-01-29", code: "KU-001-03", quantity: "10"},
];

@Component({
  selector: 'app-forecasting',
  templateUrl: './forecasting.component.html',
  styleUrls: ['./forecasting.component.sass']
})
export class ForecastingComponent implements OnInit {

  isLoggedIn = false;
  isAdmin = false;

  displayedColumns = ['date', 'code', 'quantity'];
  dataSource: MatTableDataSource<ExampleElement> = new MatTableDataSource<ExampleElement>(ELEMENT_DATA);

  isActive: boolean = false;

  rmsse = "";
  loss = "";

  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  emptySearchList = false;
  fileName: string = "";

  selectedFile?: File;
  canUploadFile = false;

  constructor(private formBuilder: FormBuilder, private forecastingService: ForecastingService,
              private translate: TranslateService, public dialog: MatDialog,
              private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    if(this.tokenStorage.getUser() && this.tokenStorage.getUser().roles.includes(ERole[ERole.ROLE_ADMIN])){
      this.isAdmin = true;
    } else {
      this.router.navigate(['/profile']).then(() => this.reloadPage());
    }
    this.loadForecastingState();
  }

  loadForecastingState(){
    this.forecastingService.checkForecastingState()
      .subscribe({
        next: (data) => {
          console.log(data);
          this.isActive = data.active
          this.rmsse = data.rmsse
          this.loss = data.loss
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("settings.forecasting.active-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }

  reloadPage(): void {
    window.location.reload();
  }

  onFileSelected(event: Event) {
    this.selectedFile = (event.target as HTMLInputElement)?.files?.[0];
    if(this.selectedFile){
      const ext = this.selectedFile.name.substring(this.selectedFile.name.lastIndexOf('.') + 1);
      this.canUploadFile = ext.toLowerCase() === 'xlsx' || ext.toLowerCase() === 'xls';
      this.fileName = this.selectedFile.name
    } else {
      this.canUploadFile = false;
      this.fileName = ''
    }
  }

  onSubmit() {
    if(this.selectedFile) {
      this.forecastingService.train(this.selectedFile!).subscribe({
        next: (data) => {
          console.log(data);
          this.selectedFile = undefined
          this.canUploadFile = false
          this.fileName = ''
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("settings.forecasting.train-success"),
            icon: 'success',
            showConfirmButton: false
          })
          this.loadForecastingState();
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("settings.forecasting.train-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      });
    }
  }

  getTranslateMessage(key: string): string{
    let message = "";
    this.translate.get(key).subscribe(data =>
      message = data
    );
    return message;
  }
}
