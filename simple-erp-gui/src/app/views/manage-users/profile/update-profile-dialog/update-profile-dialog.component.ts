import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {UpdateUserData} from "../../../../models/manage-users/UpdateUserData";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import Swal from "sweetalert2";
import {UsersService} from "../../../../services/manage-users/users.service";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-update-profile-dialog',
  templateUrl: './update-profile-dialog.component.html',
  styleUrls: ['./update-profile-dialog.component.sass']
})
export class UpdateProfileDialogComponent implements OnInit {

  form!: FormGroup;
  dataChanged = false;

  constructor(
    public dialogRef: MatDialogRef<UpdateProfileDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UpdateUserData, private formBuilder: FormBuilder,
    private usersService: UsersService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
        name: [this.data.name],
        surname: [this.data.surname],
        email: [this.data.email, Validators.email],
        phone: [this.data.phone ? this.data.phone : '', Validators.pattern('(\\+48)?\\s?[0-9]{3}\\s?[0-9]{3}\\s?[0-9]{3}')]
      })
  }

  saveData(){
    this.usersService.updateUserProfileData({
      "email": this.form.get('email')?.value,
      "name": this.form.get('name')?.value,
      "surname": this.form.get('surname')?.value,
      "phone": this.form.get('phone')?.value
    }).subscribe({
      next: (data) => {
        console.log(data);
        this.dataChanged = true
        this.form.markAsPristine();
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("manage-users.profile.update-success"),
          icon: 'success',
          showConfirmButton: false
        })
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("manage-users.profile.update-error"),
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
