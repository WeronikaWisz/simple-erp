import {Component, Inject, OnInit} from '@angular/core';
import {ProductCode} from "../../../../models/products/ProductCode";
import {Unit} from "../../../../models/products/Unit";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {EUnit} from "../../../../enums/EUnit";
import {ProductProductionInfo} from "../../../../models/production/ProductProductionInfo";
import {ProductionService} from "../../../../services/production.service";
import {ProductProductionDialogData} from "../../../../models/production/ProductProductionDialogData";
import {ProductsService} from "../../../../services/products.service";

@Component({
  selector: 'app-production-info-dialog',
  templateUrl: './production-info-dialog.component.html',
  styleUrls: ['./production-info-dialog.component.sass']
})
export class ProductionInfoDialogComponent implements OnInit {

  dataChanged = false;

  productList: ProductCode[] = [];
  units: Unit[] = [];

  productProduction: ProductProductionInfo = {
    code: "",
    name: "",
    productionSteps: [],
    unit: EUnit.PIECES,
    productSet: []
  }

  constructor(
    public dialogRef: MatDialogRef<ProductionInfoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProductProductionDialogData, private productsService: ProductsService,
    private productionService: ProductionService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.loadUnits();
    this.loadProductList();
    this.loadProduct();
  }

  loadProduct(){
    if(this.data.from == 'PRODUCTION') {
      this.productionService.getProductionInfo(this.data.id)
        .subscribe({
          next: (data) => {
            this.productProduction = data
          },
          error: (err) => {
            Swal.fire({
              position: 'top-end',
              title: this.getTranslateMessage("production.product-info.load-error"),
              text: err.error.message,
              icon: 'error',
              showConfirmButton: false
            })
          }
        })
    } else if(this.data.from == 'PRODUCTS'){
      this.productsService.getProductProduction(this.data.id)
        .subscribe({
          next: (data) => {
            this.productProduction = data
          },
          error: (err) => {
            Swal.fire({
              position: 'top-end',
              title: this.getTranslateMessage("production.product-info.load-error"),
              text: err.error.message,
              icon: 'error',
              showConfirmButton: false
            })
          }
        })
    } else if(this.data.from == 'PRODUCTION-PRODUCT'){
      this.productionService.getProductInfo(this.data.id)
        .subscribe({
          next: (data) => {
            this.productProduction = data
          },
          error: (err) => {
            Swal.fire({
              position: 'top-end',
              title: this.getTranslateMessage("production.product-info.load-error"),
              text: err.error.message,
              icon: 'error',
              showConfirmButton: false
            })
          }
        })
    }
  }

  getTranslateMessage(key: string): string{
    let message = "";
    this.translate.get(key).subscribe(data =>
      message = data
    );
    return message;
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

  loadProductList(){
    if(this.data.from == 'PRODUCTION' || this.data.from == 'PRODUCTION-PRODUCT') {
      this.productionService.loadProductList()
        .subscribe({
          next: (data) => {
            console.log(data);
            this.productList = data;
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
    } else if(this.data.from == 'PRODUCTS'){
      this.productsService.loadProductList()
        .subscribe({
          next: (data) => {
            console.log(data);
            this.productList = data;
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
  }

  getUnit(code: string) {
    const productUnit = this.productList.find(product => product.code === code)!.unit as unknown as string;
    return  this.units.find(unit => EUnit[unit.unit] === productUnit)!.shortcut
  }

}
