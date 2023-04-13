import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Unit} from "../../../../models/products/Unit";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {TradeService} from "../../../../services/trade.service";
import {DelegateExternalAcceptance} from "../../../../models/trade/DelegateExternalAcceptance";

@Component({
  selector: 'app-purchase-order-number-dialog',
  templateUrl: './purchase-order-number-dialog.component.html',
  styleUrls: ['./purchase-order-number-dialog.component.sass']
})
export class PurchaseOrderNumberDialogComponent implements OnInit {

  form!: FormGroup;
  dataChanged = false;

  units: Unit[] = [];

  constructor(
    public dialogRef: MatDialogRef<PurchaseOrderNumberDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DelegateExternalAcceptance, private formBuilder: FormBuilder,
    private tradeService: TradeService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      number: [this.data.orderNumber, Validators.required],
    })
  }

  saveData(){
    this.tradeService.delegateExternalAcceptance({
      ids: this.data.ids,
      orderNumber: this.form.get('number')?.value
    }).subscribe({
      next: (data) => {
        console.log(data);
        this.dataChanged = true
        this.form.markAsPristine();
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("trade.browse-purchases.delegate-acceptance-success"),
          icon: 'success',
          showConfirmButton: false
        })
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("trade.browse-purchases.delegate-error"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
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

}
