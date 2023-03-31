import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {WarehouseService} from "../../../../services/warehouse.service";
import {TranslateService} from "@ngx-translate/core";
import {AssignedUser} from "../../../../models/warehouse/AssignedUser";
import Swal from "sweetalert2";

@Component({
  selector: 'app-assigned-users-dialog',
  templateUrl: './assigned-users-dialog.component.html',
  styleUrls: ['./assigned-users-dialog.component.sass']
})
export class AssignedUsersDialogComponent implements OnInit {

  dataChanged = false;

  user: AssignedUser = {
    id: this.data,
    name: '',
    surname: '',
    email: '',
    phone: ''
  }

  constructor(
    public dialogRef: MatDialogRef<AssignedUsersDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: number,
    private suppliesService: WarehouseService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser(){
    this.suppliesService.loadAssignedUser(this.data)
      .subscribe({
        next: (data) => {
          this.user = data
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("supplies.delegated-tasks.user-error"),
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
