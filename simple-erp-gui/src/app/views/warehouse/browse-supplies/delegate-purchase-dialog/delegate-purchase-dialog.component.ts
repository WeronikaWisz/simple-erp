import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Unit} from "../../../../models/products/Unit";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {WarehouseService} from "../../../../services/warehouse.service";
import {TranslateService} from "@ngx-translate/core";
import {EUnit} from "../../../../enums/EUnit";
import Swal from "sweetalert2";
import {DelegatedTaskData} from "../../../../models/warehouse/DelegatedTaskData";

@Component({
  selector: 'app-delegate-purchase-dialog',
  templateUrl: './delegate-purchase-dialog.component.html',
  styleUrls: ['./delegate-purchase-dialog.component.sass']
})
export class DelegatePurchaseDialogComponent implements OnInit {

  form!: FormGroup;
  dataChanged = false;

  units: Unit[] = [];

  isEditView = false;

  constructor(
    public dialogRef: MatDialogRef<DelegatePurchaseDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DelegatedTaskData, private formBuilder: FormBuilder,
    private suppliesService: WarehouseService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if(this.data.taskId && this.data.quantity){
      this.isEditView = true;
      this.form = this.formBuilder.group({
        quantity: [this.data.quantity, Validators.required],
        days: [null]
      })
    } else {
      this.form = this.formBuilder.group({
        quantity: [null, Validators.required],
        days: [null]
      })
    }
    this.loadUnits();
  }

  saveData(){
    this.suppliesService.delegatePurchaseTask({
      id: this.data.stockLevelId!,
      quantity: this.form.get('quantity')?.value
    }).subscribe({
      next: (data) => {
        console.log(data);
        this.dataChanged = true
        this.form.markAsPristine()
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("supplies.browse-supplies.delegate-purchase-success"),
          icon: 'success',
          showConfirmButton: false
        })
        this.dialogRef.close(true);
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("supplies.browse-supplies.delegate-purchase-error"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    });
  }

  updateData(){
    this.suppliesService.updatePurchaseTask({
      id: this.data.taskId!,
      quantity: this.form.get('quantity')?.value
    }).subscribe({
      next: (data) => {
        console.log(data);
        this.dataChanged = true
        this.form.markAsPristine()
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("supplies.browse-supplies.update-purchase-success"),
          icon: 'success',
          showConfirmButton: false
        })
        this.dialogRef.close(true);
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("supplies.browse-supplies.update-purchase-error"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    });
  }

  // TODO
  suggestQuantity() {

  }

  daysNotSet(): boolean {
    return this.form.get('days')?.value === null || this.form.get('days')?.value <= 0
  }

  getTranslateMessage(key: string): string{
    let message = "";
    this.translate.get(key).subscribe(data =>
      message = data
    );
    return message;
  }

  getUnit(): string {
    const currentUnit = this.data.unit as unknown as string;
    return this.units.find(unit => EUnit[unit.unit] === currentUnit)!.shortcut
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
}
