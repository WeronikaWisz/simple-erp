import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {UpdateContractorRequest} from "../../../../models/products/UpdateContractorRequest";
import {TradeService} from "../../../../services/trade.service";
import {ProductsService} from "../../../../services/products.service";
import {ContractorDialogInfo} from "../../../../models/products/ContractorDialogInfo";

@Component({
  selector: 'app-contractor-dialog',
  templateUrl: './contractor-dialog.component.html',
  styleUrls: ['./contractor-dialog.component.sass']
})
export class ContractorDialogComponent implements OnInit {

  dataChanged = false;

  contractor: UpdateContractorRequest = {
    id: this.data.id,
    name: '',
    email: '',
    phone: '',
    buildingNumber: '',
    city: '',
    doorNumber: '',
    post: '',
    postalCode: '',
    street: '',
    accountNumber: "",
    bankAccount: "",
    country: "",
    nip: "",
    url: "",
  }

  constructor(
    public dialogRef: MatDialogRef<ContractorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ContractorDialogInfo, private productsService: ProductsService,
    private tradeService: TradeService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser(){
    if(this.data.isFromTrade){
      this.tradeService.getContractor(this.data.id)
        .subscribe({
          next: (data) => {
            this.contractor = data
          },
          error: (err) => {
            Swal.fire({
              position: 'top-end',
              title: this.getTranslateMessage("products.browse-contractors.contractor-error"),
              text: err.error.message,
              icon: 'error',
              showConfirmButton: false
            })
          }
        })
    } else {
      this.productsService.getContractor(this.data.id)
        .subscribe({
          next: (data) => {
            this.contractor = data
          },
          error: (err) => {
            Swal.fire({
              position: 'top-end',
              title: this.getTranslateMessage("products.browse-contractors.contractor-error"),
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

}
