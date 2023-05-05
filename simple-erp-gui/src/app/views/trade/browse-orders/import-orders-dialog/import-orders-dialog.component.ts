import {Component, OnInit} from '@angular/core';
import { MatDialogRef} from "@angular/material/dialog";
import {TradeService} from "../../../../services/trade.service";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";

@Component({
  selector: 'app-import-orders-dialog',
  templateUrl: './import-orders-dialog.component.html',
  styleUrls: ['./import-orders-dialog.component.sass']
})
export class ImportOrdersDialogComponent implements OnInit {

  dataChanged = false;

  fileName: string = "";

  selectedFile?: File;
  canUploadFile = false;

  constructor(
    public dialogRef: MatDialogRef<ImportOrdersDialogComponent>,
    private tradeService: TradeService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {}

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

  saveData(){
    if(this.selectedFile) {
      this.tradeService.importOrders(this.selectedFile!).subscribe({
        next: (data) => {
          console.log(data);
          this.selectedFile = undefined
          this.canUploadFile = false
          this.fileName = ''
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("trade.browse-orders.import-success"),
            icon: 'success',
            showConfirmButton: false
          })
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("trade.browse-orders.import-error"),
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
