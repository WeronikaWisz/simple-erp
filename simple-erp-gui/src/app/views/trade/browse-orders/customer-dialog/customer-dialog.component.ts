import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {CustomerData} from "../../../../models/trade/CustomerData";
import {CustomersService} from "../../../../services/customers.service";

@Component({
  selector: 'app-customer-dialog',
  templateUrl: './customer-dialog.component.html',
  styleUrls: ['./customer-dialog.component.sass']
})
export class CustomerDialogComponent implements OnInit {

  dataChanged = false;

  customer: CustomerData = {
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
    street: ''
  }

  constructor(
    public dialogRef: MatDialogRef<CustomerDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: number,
    private customersService: CustomersService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser(){
    this.customersService.getCustomerData(this.data)
      .subscribe({
        next: (data) => {
          this.customer = data
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("trade.browse-orders.customer-error"),
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

}
