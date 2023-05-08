import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/manage-users/auth.service';
import { TokenStorageService } from '../../../services/token-storage.service';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import Swal from "sweetalert2";
import {TranslateService} from "@ngx-translate/core";
import {ERole} from "../../../enums/ERole";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.sass']
})
export class LoginComponent implements OnInit {

  form!: FormGroup;
  isLoggedIn = false;
  roles: string[] = [];
  hide = true;

  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router, private translate: TranslateService,
              private authService: AuthService, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.roles = this.tokenStorage.getUser().roles;
      this.redirectToPageForRole();
    }
  }

  onSubmit(): void {

    this.authService.login({
      "username": this.form.get('username')?.value,
      "password": this.form.get('password')?.value
    }).subscribe({
      next: (data) => {
        this.tokenStorage.saveToken(data.token);
        this.tokenStorage.saveUser(data);
        console.log(data.token);
        this.isLoggedIn = true;
        this.roles = this.tokenStorage.getUser().roles;
        this.redirectToPageForRole();
      },
      error: (err) => {
        this.form.controls['username'].setErrors({'incorrect': true});
        this.form.controls['password'].setErrors({'incorrect': true});
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("manage-users.login.error-message"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    });
  }

  redirectToPageForRole(){
    if(this.roles.includes(ERole[ERole.ROLE_ADMIN])) {
      this.router.navigate(['/browse-analysis']).then(() => this.reloadPage());
    } else if (this.roles.includes(ERole[ERole.ROLE_TRADE])){
      this.router.navigate(['/browse-orders']).then(() => this.reloadPage());
    } else if (this.roles.includes(ERole[ERole.ROLE_WAREHOUSE])){
      this.router.navigate(['/browse-supplies']).then(() => this.reloadPage());
    } else if (this.roles.includes(ERole[ERole.ROLE_PRODUCTION])){
      this.router.navigate(['/browse-production']).then(() => this.reloadPage());
    } else {
      this.router.navigate(['/profile']).then(() => this.reloadPage());
    }
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
