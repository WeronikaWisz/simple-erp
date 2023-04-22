import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserName} from "../../../../models/manage-users/UserName";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TaskNumberType} from "../../../../models/trade/TaskNumberType";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {ProductionService} from "../../../../services/production.service";

@Component({
  selector: 'app-change-production-user-dialog',
  templateUrl: './change-production-user-dialog.component.html',
  styleUrls: ['./change-production-user-dialog.component.sass']
})
export class ChangeProductionUserDialogComponent implements OnInit {


  form!: FormGroup;
  dataChanged = false;

  users: UserName[] = []

  constructor(
    public dialogRef: MatDialogRef<ChangeProductionUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TaskNumberType, private formBuilder: FormBuilder,
    private productionService: ProductionService, private translate: TranslateService
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
    this.productionService.loadUsers().subscribe({
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
    this.productionService.updateAssignedUsers({
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
