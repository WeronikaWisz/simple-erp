import {Component, OnInit} from '@angular/core';
import Swal from "sweetalert2";
import {AbstractControl, FormBuilder, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../helpers/validation";
import {TranslateService} from "@ngx-translate/core";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {STEPPER_GLOBAL_OPTIONS} from "@angular/cdk/stepper";
import {ManageUsersService} from "../../../services/manage-users/manage-users.service";
import {Role} from "../../../models/auth/Role";
import {ERole} from "../../../enums/ERole";
import {UserListItem} from "../../../models/manage-users/UserListItem";

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.sass'],
  providers: [{
    provide: STEPPER_GLOBAL_OPTIONS, useValue: {showError: true}
  }]
})
export class AddUserComponent implements OnInit {

  formTitle = ""

  form!: FormGroup;
  isLoggedIn = false;
  hide = true;

  get formArray(): AbstractControl | null { return this.form.get('formArray'); }

  rolesList: Role[] = [];

  isEditUserView = false;
  userId?: number;

  changePassword = false;

  constructor(private formBuilder: FormBuilder, private manageUsersService: ManageUsersService,
              private translate: TranslateService, private route: ActivatedRoute,
              private router: Router, private tokenStorage: TokenStorageService) {
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
  }

  ngOnInit(): void {
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
    this.checkIfEditUserView();
  }

  checkIfEditUserView(){
    this.route.params
      .subscribe(
        params => {
          console.log(params);
          if (params['id']){
            this.isEditUserView = true;
            this.userId = params['id'];
            this.formTitle = this.getTranslateMessage("manage-users.register.edit-title")
            this.formArray!.get([1])!.get('password')?.clearValidators();
            this.formArray!.get([1])!.get('confirmPassword')?.clearValidators();
            this.getUser()
          } else {
            this.formTitle = this.getTranslateMessage("manage-users.register.register-title")
          }
        }
      );
  }

  getUser() {
    this.manageUsersService.getUser(this.userId!).subscribe({
      next: (data) => {
      console.log(data)
      this.fillFormWithEditedUser(data);
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("manage-users.register.load-error"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    })
  }

  fillFormWithEditedUser(data: UserListItem){
    this.formArray!.get([1])!.get('username')?.setValue(data.username);
    this.formArray!.get([0])!.get('email')?.setValue(data.email);
    this.formArray!.get([0])!.get('name')?.setValue(data.name);
    this.formArray!.get([0])!.get('surname')?.setValue(data.surname);
    this.formArray!.get([0])!.get('phone')?.setValue(data.phone);
    this.formArray!.get([1])!.get('roles')?.setValue(data.roles);
  }

  updateUser(): void {
    this.manageUsersService.updateUser({
      "id": this.userId!,
      "username": this.formArray!.get([1])!.get('username')?.value,
      "email": this.formArray!.get([0])!.get('email')?.value,
      "name": this.formArray!.get([0])!.get('name')?.value,
      "surname": this.formArray!.get([0])!.get('surname')?.value,
      "phone": this.formArray!.get([0])!.get('phone')?.value,
      "roles": this.formArray!.get([1])!.get('roles')?.value,
      "password": this.formArray!.get([1])!.get('password')?.value
    }).subscribe({
      next: (data) => {
        console.log(data);
        this.router.navigate(['/browse-users']).then(() => this.showSuccess("manage-users.register.update-success"));
      },
      error: (err) => {
        if(err.error.message.includes("e-mail")){
          this.form.controls['formArray'].get([0])?.get('email')?.setErrors({'incorrect': true})
        } else if(err.error.message.includes("Nazwa") || err.error.message.includes("Username")){
          this.form.controls['formArray'].get([1])?.get('username')?.setErrors({'incorrect': true})
        }
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("manage-users.register.update-error"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    });
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
      this.router.navigate(['/add-user']).then(() => this.showSuccess("manage-users.register.register-success"));
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

  editPassword(event: any){
    this.changePassword = event.checked;
    if(event.checked){
      this.formArray!.get([1])!.get('password')?.addValidators(Validators.required)
      this.formArray!.get([1])!.get('confirmPassword')?.addValidators(Validators.required)
    } else {
      this.formArray!.get([1])!.get('password')?.clearValidators();
      this.formArray!.get([1])!.get('confirmPassword')?.clearValidators();
    }
  }

  showSuccess(key: string): void {
    Swal.fire({
      position: 'top-end',
      title: this.getTranslateMessage(key),
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
