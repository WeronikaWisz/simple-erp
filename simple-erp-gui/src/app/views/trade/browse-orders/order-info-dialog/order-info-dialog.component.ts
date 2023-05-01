import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {TradeService} from "../../../../services/trade.service";
import {UpdateOrderRequest} from "../../../../models/trade/UpdateOrderRequest";
import {EUnit} from "../../../../enums/EUnit";
import {ProductCode} from "../../../../models/products/ProductCode";
import {Unit} from "../../../../models/products/Unit";

@Component({
  selector: 'app-order-info-dialog',
  templateUrl: './order-info-dialog.component.html',
  styleUrls: ['./order-info-dialog.component.sass']
})
export class OrderInfoDialogComponent implements OnInit {

  dataChanged = false;

  productList: ProductCode[] = [];
  units: Unit[] = [];

  order: UpdateOrderRequest = {
    id: this.data,
    name: '',
    surname: '',
    email: '',
    phone: '',
    buildingNumber: '',
    city: '',
    doorNumber: '',
    post: '',
    postalCode: '',
    street: '',
    delivery: "",
    discount: "",
    number: "",
    orderDate: new Date(),
    price: "",
    productSet: [],
  }

  constructor(
    public dialogRef: MatDialogRef<OrderInfoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: number,
    private tradeService: TradeService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.loadUnits();
    this.loadProductForSetList();
    this.loadOrder();
  }

  loadOrder(){
    this.tradeService.getOrder(this.data)
      .subscribe({
        next: (data) => {
          this.order = data
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("trade.add-order.load-error"),
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

  loadProductForSetList(){
    this.tradeService.loadOrderProductList()
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
