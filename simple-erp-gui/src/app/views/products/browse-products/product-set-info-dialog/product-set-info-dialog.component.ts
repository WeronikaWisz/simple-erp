import {Component, Inject, OnInit} from '@angular/core';
import {ProductCode} from "../../../../models/products/ProductCode";
import {Unit} from "../../../../models/products/Unit";
import {EUnit} from "../../../../enums/EUnit";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ProductsService} from "../../../../services/products.service";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {ProductSetInfo} from "../../../../models/products/ProductSetInfo";

@Component({
  selector: 'app-product-set-info-dialog',
  templateUrl: './product-set-info-dialog.component.html',
  styleUrls: ['./product-set-info-dialog.component.sass']
})
export class ProductSetInfoDialogComponent implements OnInit {


  dataChanged = false;

  productList: ProductCode[] = [];
  units: Unit[] = [];

  productSet: ProductSetInfo = {
    code: "",
    name: "",
    productSet: []
  }

  constructor(
    public dialogRef: MatDialogRef<ProductSetInfoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: number, private productsService: ProductsService,
    private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.loadUnits();
    this.loadProductList();
    this.loadProduct();
  }

  loadProduct(){
      this.productsService.getProductSet(this.data)
        .subscribe({
          next: (data) => {
            this.productSet = data
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

  getUnit(code: string) {
    const productUnit = this.productList.find(product => product.code === code)!.unit as unknown as string;
    return  this.units.find(unit => EUnit[unit.unit] === productUnit)!.shortcut
  }

}
