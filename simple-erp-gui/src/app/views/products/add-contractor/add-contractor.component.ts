import { Component, OnInit } from '@angular/core';
import Swal from "sweetalert2";
import {AbstractControl, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import {ProductsService} from "../../../services/products.service";

@Component({
  selector: 'app-add-contractor',
  templateUrl: './add-contractor.component.html',
  styleUrls: ['./add-contractor.component.sass']
})
export class AddContractorComponent implements OnInit {


  form!: FormGroup;
  isLoggedIn = false;
  isAdmin = false;
  hide = true;

  get formArray(): AbstractControl | null { return this.form.get('formArray'); }

  constructor(private formBuilder: FormBuilder, private productsService: ProductsService, private translate: TranslateService,
              private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      formArray: this.formBuilder.array([
        this.formBuilder.group({
          name: ['', Validators.required],
          country: ['', Validators.required],
          nip: ['', [Validators.required, Validators.pattern('[0-9]{10}')]],
          email: ['', [Validators.required, Validators.email]],
          phone: ['', Validators.pattern('(\\+48)?\\s?[0-9]{3}\\s?[0-9]{3}\\s?[0-9]{3}')],
          url: [''],
        }),
        this.formBuilder.group({
          postalCode: ['', [Validators.required, Validators.pattern('[0-9]{2}-[0-9]{3}')]],
          post: ['', Validators.required],
          city: ['', Validators.required],
          street: ['', Validators.required],
          buildingNumber: ['', [Validators.required, Validators.pattern('[0-9]+.*')]],
          doorNumber: ['', Validators.pattern('[0-9]+.*')]
        }),
        this.formBuilder.group({
          bankAccount: [''],
          accountNumber: ['', Validators.pattern('[0-9]*')],
          }
        )
      ])
    });
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
    this.productsService.addContractor({
      "name": this.formArray!.get([0])!.get('name')?.value,
      "country": this.formArray!.get([0])!.get('country')?.value,
      "nip": this.formArray!.get([0])!.get('nip')?.value,
      "email": this.formArray!.get([0])!.get('email')?.value,
      "phone": this.formArray!.get([0])!.get('phone')?.value,
      "url": this.formArray!.get([0])!.get('url')?.value,
      "postalCode": this.formArray!.get([1])!.get('postalCode')?.value,
      "post": this.formArray!.get([1])!.get('post')?.value,
      "city": this.formArray!.get([1])!.get('city')?.value,
      "street": this.formArray!.get([1])!.get('street')?.value,
      "buildingNumber": this.formArray!.get([1])!.get('buildingNumber')?.value,
      "doorNumber": this.formArray!.get([1])!.get('doorNumber')?.value,
      "bankAccount": this.formArray!.get([2])!.get('bankAccount')?.value,
      "accountNumber": this.formArray!.get([2])!.get('accountNumber')?.value
    }).subscribe({
      next: (data) => {
        console.log(data);
        this.router.navigate(['/add-contractor']).then(() => this.showSuccess());
      },
        error: (err) => {
        if(err.error.message.includes("e-mail")){
          this.form.controls['formArray'].get([0])?.get('email')?.setErrors({'incorrect': true})
        }
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("products.add-contractor.register-error"),
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
      title: this.getTranslateMessage("products.add-contractor.register-success"),
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
