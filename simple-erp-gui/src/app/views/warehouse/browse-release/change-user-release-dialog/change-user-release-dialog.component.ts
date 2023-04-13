import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserName} from "../../../../models/manage-users/UserName";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {WarehouseService} from "../../../../services/warehouse.service";
import {TaskNumberType} from "../../../../models/trade/TaskNumberType";

@Component({
  selector: 'app-change-user-release-dialog',
  templateUrl: './change-user-release-dialog.component.html',
  styleUrls: ['./change-user-release-dialog.component.sass']
})
export class ChangeUserReleaseDialogComponent implements OnInit {


  form!: FormGroup;
  dataChanged = false;

  users: UserName[] = []

  constructor(
    public dialogRef: MatDialogRef<ChangeUserReleaseDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TaskNumberType, private formBuilder: FormBuilder,
    private warehouseService: WarehouseService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      assignedUser: ['', Validators.required],
    })
    this.loadUsers();
  }

  loadUsers(){
    this.warehouseService.loadUsers().subscribe({
      next: (data) => {
        console.log(data);
        this.users = data;
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("manage-users.default-users.load-error-2"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    });
  }

  saveData(){
    this.warehouseService.updateAssignedUsers({
      "taskIds": this.data.taskIds,
      "employeeId": this.form.get('assignedUser')?.value,
      "task": this.data.task
    }).subscribe({
      next: (data) => {
        console.log(data);
        this.dataChanged = true
        this.form.markAsPristine();
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("trade.browse-orders.update-success"),
          icon: 'success',
          showConfirmButton: false
        })
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("trade.browse-orders.update-error"),
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
