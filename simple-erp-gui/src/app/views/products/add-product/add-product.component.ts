import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ManageUsersService} from "../../../services/manage-users/manage-users.service";
import {TranslateService} from "@ngx-translate/core";
import {ActivatedRoute, Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import Swal from "sweetalert2";

@Component({
  selector: 'app-add-product',
  templateUrl: './add-product.component.html',
  styleUrls: ['./add-product.component.sass']
})
export class AddProductComponent implements OnInit {

  formTitle = ""

  form!: FormGroup;
  isLoggedIn = false;
  isAdmin = false;
  hide = true;

  constructor(private formBuilder: FormBuilder, private manageUsersService: ManageUsersService,
              private translate: TranslateService, private route: ActivatedRoute,
              private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      code: ['', Validators.required],
      name: ['', Validators.required]
    });
    this.formTitle = this.getTranslateMessage("products.add-product.product-title")
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    if(this.tokenStorage.getUser() && this.tokenStorage.getUser().roles.includes(ERole[ERole.ROLE_ADMIN])){
      this.isAdmin = true;
    } else {
      this.router.navigate(['/profile']).then(() => this.reloadPage());
    }
  }

  onSubmit(): void {
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
