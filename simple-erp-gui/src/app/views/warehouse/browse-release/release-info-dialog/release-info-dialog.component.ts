import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {ProductCode} from "../../../../models/products/ProductCode";
import {Unit} from "../../../../models/products/Unit";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {EUnit} from "../../../../enums/EUnit";
import {ReleaseDetails} from "../../../../models/warehouse/ReleaseDetails";
import {EDirection} from "../../../../enums/EDirection";
import {WarehouseService} from "../../../../services/warehouse.service";
import {TradeService} from "../../../../services/trade.service";
import {ReleaseAcceptanceDialogData} from "../../../../models/warehouse/ReleaseAcceptanceDialogData";
import {ProductionService} from "../../../../services/production.service";

declare var require: any;

import * as pdfMake from "pdfmake/build/pdfmake";
import * as pdfFonts from "pdfmake/build/vfs_fonts";
const htmlToPdfmake = require("html-to-pdfmake");
(pdfMake as any).vfs = pdfFonts.pdfMake.vfs;

@Component({
  selector: 'app-release-info-dialog',
  templateUrl: './release-info-dialog.component.html',
  styleUrls: ['./release-info-dialog.component.sass']
})
export class ReleaseInfoDialogComponent implements OnInit {

  dataChanged = false;

  productList: ProductCode[] = [];
  units: Unit[] = [];

  release: ReleaseDetails = {
    id: this.data.id,
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
    number: "",
    orderDate: "",
    productSet: [],
    association: "",
    direction: EDirection.EXTERNAL,
    executionDate: "",
  }

  @ViewChild('pdfTable') pdfTable!: ElementRef;

  constructor(
    public dialogRef: MatDialogRef<ReleaseInfoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ReleaseAcceptanceDialogData, private tradeService: TradeService,
    private warehouseService: WarehouseService, private translate: TranslateService,
    private productionService: ProductionService
  ) {
    dialogRef.disableClose = true;

  }

  ngOnInit(): void {
    this.loadUnits();
    this.loadProductForSetList();
    this.loadRelease();
  }

  loadRelease(){
    if(this.data.from == 'WAREHOUSE') {
      this.warehouseService.getRelease(this.data.id)
        .subscribe({
          next: (data) => {
            this.release = data
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
    } else if(this.data.from == 'TRADE') {
      this.tradeService.getRelease(this.data.id)
        .subscribe({
          next: (data) => {
            this.release = data
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
    } else if(this.data.from == 'PRODUCTION') {
      this.productionService.getRelease(this.data.id)
        .subscribe({
          next: (data) => {
            this.release = data
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
    if(this.data.from == 'WAREHOUSE') {
      this.warehouseService.loadProductList()
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
    } else if(this.data.from == 'TRADE') {
      this.tradeService.loadProductList()
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
    } else if(this.data.from == 'PRODUCTION') {
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
    }
  }

  getUnit(code: string) {
    const productUnit = this.productList.find(product => product.code === code)!.unit as unknown as string;
    return this.units.find(unit => EUnit[unit.unit] === productUnit)!.shortcut
  }

  getDirection(eDirection: EDirection): string {
    let direction = eDirection as unknown as string;
    if(direction === EDirection[EDirection.EXTERNAL]){
      return this.getTranslateMessage("supplies.browse-release.external")
    } else {
      return this.getTranslateMessage("supplies.browse-release.internal")
    }
  }

  isExternal(eDirection: EDirection): boolean {
    let direction = eDirection as unknown as string;
    return direction === EDirection[EDirection.EXTERNAL];
  }

  isStatusDone(): boolean {
    return this.release.executionDate != "";
  }

  public downloadAsPDF() {
    const pdfTable = this.pdfTable.nativeElement;
    var html = htmlToPdfmake(pdfTable.innerHTML);
    var title = this.release.number
    const documentDefinition = { content: html};
    pdfMake.createPdf(documentDefinition).download(title);
  }
}
