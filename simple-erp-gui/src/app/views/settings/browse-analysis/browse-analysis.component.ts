import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ProductsService} from "../../../services/products.service";
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import {Chart, ChartItem} from 'chart.js/auto';
import Swal from "sweetalert2";
import {ChartData} from "../../../models/trade/ChartData";
import {TradeService} from "../../../services/trade.service";

@Component({
  selector: 'app-browse-analysis',
  templateUrl: './browse-analysis.component.html',
  styleUrls: ['./browse-analysis.component.sass']
})
export class BrowseAnalysisComponent implements OnInit {

  isLoggedIn = false;
  isAdmin = false;

  public chart: any;

  todayDate = new Date();

  quantityData: ChartData[] = [];
  saleData: ChartData[] = [];
  purchaseData: ChartData[] = [];

  quantityToday = "";
  quantityMonth = "";
  saleToday = "";
  saleMonth = "";
  purchaseToday = "";
  purchaseMonth = "";

  @ViewChild('saleChart') saleChartElementRef!: ElementRef;
  @ViewChild('quantityChart') quantityChartElementRef!: ElementRef;
  @ViewChild('purchaseChart') purchaseChartElementRef!: ElementRef;

  constructor(private productsService: ProductsService, private translate: TranslateService,
              private router: Router, private tokenStorage: TokenStorageService,
              private tradeService: TradeService) { }

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
    this.tradeService.getSaleAndExpenses()
      .subscribe({
        next: (data) => {
          console.log(data);
          this.quantityData = data.quantity;
          this.saleData = data.sale;
          this.purchaseData = data.purchase;
          this.quantityToday = data.quantityToday;
          this.quantityMonth = data.quantityMonth;
          this.saleToday = data.saleToday;
          this.saleMonth = data.saleMonth;
          this.purchaseToday = data.purchaseToday;
          this.purchaseMonth = data.purchaseMonth;
          this.makeCharts()
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("settings.browse-analysis.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }

  makeCharts(): void {
    this.createChart(this.quantityChartElementRef.nativeElement,
      this.getTranslateMessage("settings.browse-analysis.quantity-chart"),
      this.getTranslateMessage("settings.browse-analysis.quantity-label"),
      '#6495ed', this.quantityData.map(d => d.label), this.quantityData.map(d => d.data.toString()), "szt.");
    this.createChart(this.saleChartElementRef.nativeElement,
      this.getTranslateMessage("settings.browse-analysis.sale-chart"),
      this.getTranslateMessage("settings.browse-analysis.sale-label"),
      '#2E8B57FF', this.saleData.map(d => d.label), this.saleData.map(d => d.data.toString()), "zł");
    this.createChart(this.purchaseChartElementRef.nativeElement,
      this.getTranslateMessage("settings.browse-analysis.purchase-chart"),
      this.getTranslateMessage("settings.browse-analysis.purchase-label"),
      '#CD5C5CFF', this.purchaseData.map(d => d.label), this.purchaseData.map(d => d.data.toString()), "zł");
  }

  createChart(context: ChartItem, title: string, label: string, color: string, labels: string[], data: string[],
              yAxisTitle: string){

    this.chart = new Chart(context, {
      type: 'line',

      data: {
        labels: labels,
        datasets: [
          {
            label: label,
            data: data,
            borderColor: color,
            borderWidth: 3,
          }
        ]
      },
      options: {
        plugins: {
          title: {
            display: true,
            text: title,
            font: {
              size: 16
            }
          },
          legend: {
            display: false,
          }
        },
        scales: {
          y: {
            title: {
              display: true,
              text: yAxisTitle
            }
          }
        },
        aspectRatio:2.5
      }

    });
  }

  getTranslateMessage(key: string): string{
    let message = "";
    this.translate.get(key).subscribe(data =>
      message = data
    );
    return message;
  }

  reloadPage(): void {
    window.location.reload();
  }

}
