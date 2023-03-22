import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatDialogRef} from "@angular/material/dialog";
import {UsersService} from "../../../services/manage-users/users.service";
import Validation from "../../../helpers/validation";
// import Swal from "sweetalert2";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrls: ['./change-password-dialog.component.sass']
})
export class ChangePasswordDialogComponent implements OnInit {

  form!: FormGroup;
  hide = true;

  constructor(
    public dialogRef: MatDialogRef<ChangePasswordDialogComponent>,
    private formBuilder: FormBuilder, private translate: TranslateService,
    private usersService: UsersService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
        oldPassword: [
          '',
          [
            Validators.required
          ]
        ],
        password: [
          '',
          [
            Validators.required,
            Validators.minLength(12),
            Validators.maxLength(40)
          ]
        ],
        confirmPassword: ['', Validators.required]
      },
      {
        validators: [Validation.match('password', 'confirmPassword')]
      }
    )
  }

  onNoClick() {
    this.dialogRef.close();
  }

  changePassword(){
    this.usersService.changePassword({
      "oldPassword": this.form.get('oldPassword')?.value,
      "newPassword": this.form.get('password')?.value,
    }).subscribe({
      next: (data) => {
        console.log(data);
        // Swal.fire({
        //   position: 'top-end',
        //   title: this.getTranslateMessage("manage-users.profile.password-success"),
        //   icon: 'success',
        //   showConfirmButton: false
        // })
        // this.form.reset();
        // this.form.markAsUntouched();
      },
      error: (err) => {
        // Swal.fire({
        //   position: 'top-end',
        //   title: this.getTranslateMessage("manage-users.profile.password-error"),
        //   text: err.error.message,
        //   icon: 'error',
        //   showConfirmButton: false
        // })
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
