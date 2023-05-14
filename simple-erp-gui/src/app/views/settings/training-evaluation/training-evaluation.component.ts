import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ERole} from "../../../enums/ERole";
import Swal from "sweetalert2";
import {ForecastingService} from "../../../services/forecasting.service";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ChartData} from "../../../models/trade/ChartData";
import {Chart} from "chart.js/auto";
import {ProductCode} from "../../../models/products/ProductCode";
import {EUnit} from "../../../enums/EUnit";
import {Unit} from "../../../models/products/Unit";

@Component({
  selector: 'app-training-evaluation',
  templateUrl: './training-evaluation.component.html',
  styleUrls: ['./training-evaluation.component.sass']
})
export class TrainingEvaluationComponent implements OnInit {

  isLoggedIn = false;
  isAdmin = false;

  isActive: boolean = false;

  realData: ChartData[] = [];
  forecastData: ChartData[] = [];

  public chart: any;

  productList: ProductCode[] = [];
  product: string = "";
  yAxisTitle = "";

  units: Unit[] = [];

  @ViewChild('chart') chartElementRef!: ElementRef;

  constructor(private forecastingService: ForecastingService,
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
    this.loadUnits();
    this.loadForecastingState();
  }

  loadForecastingState(){
    this.forecastingService.checkForecastingState()
      .subscribe({
        next: (data) => {
          console.log(data);
          this.isActive = data.active
          if(!data.active){
            this.router.navigate(['/forecasting']).then(() => this.reloadPage());
          }
          this.loadProductList()
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("settings.forecasting.active-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
          this.router.navigate(['/forecasting']).then(() => this.reloadPage());
        }
      })
  }

  loadProductList(){
    this.forecastingService.loadProductList()
      .subscribe({
        next: (data) => {
          console.log(data);
          this.productList = data;
          this.product = data[0].code;
          this.changeUnit();
          this.loadProductEvaluation();
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("products.add-product.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }

  loadProductEvaluation(){
    this.forecastingService.getTrainingEvaluation(this.product)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.realData = data.real;
          this.forecastData = data.forecast;
          this.makeChart()
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("settings.training-evaluation.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }

  makeChart(){
    this.chart = new Chart(this.chartElementRef.nativeElement, {
      type: 'line',

      data: {
        labels: this.realData.map(d => d.label),
        datasets: [
          {
            label: this.getTranslateMessage("settings.training-evaluation.chart-real"),
            data: this.realData.map(d => d.data.toString()),
            borderColor: '#2E8B57FF',
            borderWidth: 3,
          },
          {
            label: this.getTranslateMessage("settings.training-evaluation.chart-forecast"),
            data: this.forecastData.map(d => d.data.toString()),
            borderColor: '#6495ed',
            borderWidth: 3,
          }
        ]
      },
      options: {
        plugins: {
          title: {
            display: true,
            text: this.getTranslateMessage("settings.training-evaluation.chart-title"),
            font: {
              size: 16
            }
          },
          legend: {
            display: true,
            position: "bottom",
            align: "start"
          }
        },
        scales: {
          y: {
            title: {
              display: true,
              text: this.yAxisTitle
            }
          }
        },
        aspectRatio:2.5
      }

    });
  }

  reloadPage(): void {
    window.location.reload();
  }

  getTranslateMessage(key: string): string{
    let message = "";
    this.translate.get(key).subscribe(data =>
      message = data
    );
    return message;
  }

  changeProduct() {
    this.chart.destroy();
    this.changeUnit();
    this.loadProductEvaluation();
  }

  loadUnits(){
    this.units = [
      {
        unit: EUnit.PIECES,
        name: this.getTranslateMessage("products.unit-pieces"),
        shortcut: this.getTranslateMessage("products.unit-pieces-s")
      },
      {
        unit: EUnit.LITERS,
        name: this.getTranslateMessage("products.unit-l"),
        shortcut: this.getTranslateMessage("products.unit-l-s")
      },
      {
        unit: EUnit.KILOGRAMS,
        name: this.getTranslateMessage("products.unit-kg"),
        shortcut: this.getTranslateMessage("products.unit-kg-s")
      },
      {
        unit: EUnit.METERS,
        name: this.getTranslateMessage("products.unit-m"),
        shortcut: this.getTranslateMessage("products.unit-m-s")
      },
      {
        unit: EUnit.SQUARE_METERS,
        name: this.getTranslateMessage("products.unit-m2"),
        shortcut: this.getTranslateMessage("products.unit-m2-s")
      }];
  }

  changeUnit() {
    const productUnit = this.productList.find(product => product.code === this.product)!.unit as unknown as string;
    this.yAxisTitle = this.units.find(unit => EUnit[unit.unit] === productUnit)!.shortcut;
  }


}
