import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Unit} from "../../../../models/products/Unit";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {WarehouseService} from "../../../../services/warehouse.service";
import {TranslateService} from "@ngx-translate/core";
import {EUnit} from "../../../../enums/EUnit";
import Swal from "sweetalert2";
import {DelegatedTaskData} from "../../../../models/warehouse/DelegatedTaskData";
import {EType} from "../../../../enums/EType";

@Component({
  selector: 'app-delegate-purchase-dialog',
  templateUrl: './delegate-purchase-dialog.component.html',
  styleUrls: ['./delegate-purchase-dialog.component.sass']
})
export class DelegatePurchaseDialogComponent implements OnInit {

  form!: FormGroup;
  dataChanged = false;

  units: Unit[] = [];

  type: EType = EType.BOUGHT;

  isEditView = false;

  formTitle = ""

  suggestion = ""

  forecastingActive = false;

  constructor(
    public dialogRef: MatDialogRef<DelegatePurchaseDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DelegatedTaskData, private formBuilder: FormBuilder,
    private suppliesService: WarehouseService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
    let type = this.data.taskType as unknown as string;
    console.info(type)
    console.info(this.data.taskType)
    if(this.data.taskType === EType.PRODUCED || type === EType[EType.PRODUCED]){
      this.type = EType.PRODUCED
    }
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
    this.setFormTitle();
    if(this.type === EType.BOUGHT){
      this.suggestion = this.getTranslateMessage("supplies.browse-supplies.suggestion")
    } else {
      this.suggestion = this.getTranslateMessage("supplies.browse-supplies.suggestion-production")
    }
    this.loadUnits();
    this.checkProductForecastingState();
  }

  checkProductForecastingState(){
    if(!this.isEditView) {
      this.suppliesService.checkProductForecastingState(this.data.stockLevelId!)
        .subscribe({
          next: (data) => {
            console.log(data);
            this.forecastingActive = data.active
          },
          error: (err) => {
            Swal.fire({
              position: 'top-end',
              title: this.getTranslateMessage("settings.forecasting.active-error"),
              text: err.error.message,
              icon: 'error',
              showConfirmButton: false
            })
          }
        })
    } else {
      this.suppliesService.checkTaskForecastingState(this.type, this.data.taskId!)
        .subscribe({
          next: (data) => {
            console.log(data);
            this.forecastingActive = data.active
          },
          error: (err) => {
            Swal.fire({
              position: 'top-end',
              title: this.getTranslateMessage("settings.forecasting.active-error"),
              text: err.error.message,
              icon: 'error',
              showConfirmButton: false
            })
          }
        })
    }
  }

  setFormTitle(){
    if(this.isEditView) {
      if(this.type === EType.BOUGHT){
        this.formTitle = this.getTranslateMessage("supplies.browse-supplies.purchase-task-edit")
      } else {
        this.formTitle = this.getTranslateMessage("supplies.browse-supplies.production-task-edit")
      }
    } else {
      if(this.type === EType.BOUGHT){
        this.formTitle = this.getTranslateMessage("supplies.browse-supplies.purchase-task")
      } else {
        this.formTitle = this.getTranslateMessage("supplies.browse-supplies.production-task")
      }
    }
  }

  saveData(){
    if(this.type === EType.BOUGHT) {
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
    } else {
      this.suppliesService.delegateProductionTask({
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
  }

  updateData(){
    if(this.type === EType.BOUGHT) {
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
    } else {
      this.suppliesService.updateProductionTask({
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
  }

  suggestQuantity() {
    if(!this.isEditView) {
      this.suppliesService.suggestProductStockQuantity(this.data.stockLevelId!)
        .subscribe({
          next: (data) => {
            console.log(data);
            this.form.get('quantity')?.setValue(data.quantity)
          },
          error: (err) => {
            Swal.fire({
              position: 'top-end',
              title: this.getTranslateMessage("settings.forecasting.active-error"),
              text: err.error.message,
              icon: 'error',
              showConfirmButton: false
            })
          }
        })
    } else {
      this.suppliesService.suggestProductTaskQuantity(this.type, this.data.taskId!)
        .subscribe({
          next: (data) => {
            console.log(data);
            this.form.get('quantity')?.setValue(data.quantity)
          },
          error: (err) => {
            Swal.fire({
              position: 'top-end',
              title: this.getTranslateMessage("settings.forecasting.active-error"),
              text: err.error.message,
              icon: 'error',
              showConfirmButton: false
            })
          }
        })
    }
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

  isBoughtType(): boolean{
    return this.type === EType.BOUGHT;
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
