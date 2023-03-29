import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {SuppliesService} from "../../../../services/supplies.service";
import {UpdateSuppliesData} from "../../../../models/warehouse/UpdateSuppliesData";
import {EUnit} from "../../../../enums/EUnit";
import {Unit} from "../../../../models/products/Unit";

@Component({
  selector: 'app-update-supplies-dialog',
  templateUrl: './update-supplies-dialog.component.html',
  styleUrls: ['./update-supplies-dialog.component.sass']
})
export class UpdateSuppliesDialogComponent implements OnInit {

  form!: FormGroup;
  dataChanged = false;

  units: Unit[] = [];

  constructor(
    public dialogRef: MatDialogRef<UpdateSuppliesDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UpdateSuppliesData, private formBuilder: FormBuilder,
    private suppliesService: SuppliesService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      quantity: [this.data.quantity, Validators.required],
      minQuantity: [this.data.minQuantity, Validators.required],
      days: [this.data.days, Validators.required]
    })
    this.loadUnits();
  }

  saveData(){
    this.suppliesService.updateSupplies({
        id: this.data.id,
        quantity: this.form.get('quantity')?.value,
        minQuantity: this.form.get('minQuantity')?.value,
        days: this.form.get('days')?.value
      }).subscribe({
      next: (data) => {
        console.log(data);
        this.dataChanged = true
        this.form.markAsPristine();
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("supplies.browse-supplies.update-success"),
          icon: 'success',
          showConfirmButton: false
        })
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("supplies.browse-supplies.update-error"),
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
