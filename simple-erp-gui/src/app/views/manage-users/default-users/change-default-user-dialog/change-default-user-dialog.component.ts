import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {ManageUsersService} from "../../../../services/manage-users/manage-users.service";
import Swal from "sweetalert2";
import {DefaultUser} from "../../../../models/manage-users/DefaultUser";
import {UserName} from "../../../../models/manage-users/UserName";

@Component({
  selector: 'app-change-default-user-dialog',
  templateUrl: './change-default-user-dialog.component.html',
  styleUrls: ['./change-default-user-dialog.component.sass']
})
export class ChangeDefaultUserDialogComponent implements OnInit {

  form!: FormGroup;
  dataChanged = false;

  users: UserName[] = []

  constructor(
    public dialogRef: MatDialogRef<ChangeDefaultUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DefaultUser, private formBuilder: FormBuilder,
    private manageUsersService: ManageUsersService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      defaultUser: [this.data.employeeId, Validators.required],
    })
    this.loadUsers();
  }

  loadUsers(){
    this.manageUsersService.loadUserForTask(this.data.taskId).subscribe({
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
    this.manageUsersService.updateDefaultUser({
      "taskId": this.data.taskId,
      "employeeId": this.form.get('defaultUser')?.value,
    }).subscribe({
      next: (data) => {
        console.log(data);
        this.dataChanged = true
        this.form.markAsPristine();
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("manage-users.default-users.update-success"),
          icon: 'success',
          showConfirmButton: false
        })
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("manage-users.default-users.update-error"),
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
