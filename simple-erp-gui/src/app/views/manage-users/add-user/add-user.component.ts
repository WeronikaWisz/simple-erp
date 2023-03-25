import {Component, OnInit} from '@angular/core';
import Swal from "sweetalert2";
import {AbstractControl, FormBuilder, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../helpers/validation";
import {TranslateService} from "@ngx-translate/core";
import {TokenStorageService} from "../../../services/token-storage.service";
import {Router} from "@angular/router";
import {STEPPER_GLOBAL_OPTIONS} from "@angular/cdk/stepper";
import {ManageUsersService} from "../../../services/manage-users/manage-users.service";
import {Role} from "../../../models/auth/Role";
import {ERole} from "../../../enums/ERole";

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.sass'],
  providers: [{
    provide: STEPPER_GLOBAL_OPTIONS, useValue: {showError: true}
  }]
})
export class AddUserComponent implements OnInit {

  form!: FormGroup;
  isLoggedIn = false;
  hide = true;

  get formArray(): AbstractControl | null { return this.form.get('formArray'); }

  rolesList: Role[] = [];

  constructor(private formBuilder: FormBuilder, private manageUsersService: ManageUsersService,
              private translate: TranslateService,
              private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    this.rolesList = [
      {
        role: ERole[ERole.ROLE_WAREHOUSE],
        name : this.getTranslateMessage("manage-users.register.role-warehouse")
      },
      {
        role: ERole[ERole.ROLE_PRODUCTION],
        name : this.getTranslateMessage("manage-users.register.role-production")
      },
      {
        role: ERole[ERole.ROLE_TRADE],
        name : this.getTranslateMessage("manage-users.register.role-trade")
      }
    ]
    this.form = this.formBuilder.group({
      formArray: this.formBuilder.array([
        this.formBuilder.group({
          name: ['', Validators.required],
          surname: ['', Validators.required],
          email: ['', [Validators.required, Validators.email]],
          phone: ['', Validators.pattern('(\\+48)?\\s?[0-9]{3}\\s?[0-9]{3}\\s?[0-9]{3}')]
        }),
        this.formBuilder.group({
            username: [
              '',
              [
                Validators.required,
                Validators.minLength(6),
                Validators.maxLength(20)
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
            confirmPassword: ['', Validators.required],
            roles: [null, Validators.required]
          },
          {
            validators: [Validation.match('password', 'confirmPassword')]
          }
        )
      ])
    });
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
  }

  onSubmit(): void {
    this.manageUsersService.addUser({
      "username": this.formArray!.get([1])!.get('username')?.value,
      "email": this.formArray!.get([0])!.get('email')?.value,
      "password": this.formArray!.get([1])!.get('password')?.value,
      "name": this.formArray!.get([0])!.get('name')?.value,
      "surname": this.formArray!.get([0])!.get('surname')?.value,
      "phone": this.formArray!.get([0])!.get('phone')?.value,
      "roles": this.formArray!.get([1])!.get('roles')?.value
    }).subscribe({
    next: (data) => {
      console.log(data);
      this.router.navigate(['/add-user']).then(() => this.showSuccess());
    },
    error: (err) => {
      if(err.error.message.includes("e-mail")){
        this.form.controls['formArray'].get([0])?.get('email')?.setErrors({'incorrect': true})
      } else if(err.error.message.includes("Nazwa") || err.error.message.includes("Username")){
        this.form.controls['formArray'].get([1])?.get('username')?.setErrors({'incorrect': true})
      }
      Swal.fire({
        position: 'top-end',
        title: this.getTranslateMessage("manage-users.register.register-error"),
        text: err.error.message,
        icon: 'error',
        showConfirmButton: false
      })
    }
  });
  }

  showSuccess(): void {
    Swal.fire({
      position: 'top-end',
      title: this.getTranslateMessage("manage-users.register.register-success"),
      icon: 'success',
      showConfirmButton: false,
      timer: 6000
    })
  }

  reloadPage(): void {
    window.location.reload();
  }

  getTranslateMessage(key: string): string{
    let message = "";
    this.translate.get(key).subscribe(data =>
      message = data
    );
    return message;
  }

}
