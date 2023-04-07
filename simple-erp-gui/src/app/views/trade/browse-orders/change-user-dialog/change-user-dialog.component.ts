import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserName} from "../../../../models/manage-users/UserName";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import Swal from "sweetalert2";
import {TradeService} from "../../../../services/trade.service";

@Component({
  selector: 'app-change-user-dialog',
  templateUrl: './change-user-dialog.component.html',
  styleUrls: ['./change-user-dialog.component.sass']
})
export class ChangeUserDialogComponent implements OnInit {

  form!: FormGroup;
  dataChanged = false;

  users: UserName[] = []

  constructor(
    public dialogRef: MatDialogRef<ChangeUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: number[], private formBuilder: FormBuilder,
    private tradeService: TradeService, private translate: TranslateService
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
    this.tradeService.loadUsers().subscribe({
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
    this.tradeService.updateAssignedUsers({
      "taskIds": this.data,
      "employeeId": this.form.get('assignedUser')?.value,
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
