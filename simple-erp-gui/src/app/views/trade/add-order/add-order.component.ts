import { Component, OnInit } from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Unit} from "../../../models/products/Unit";
import {Type} from "../../../models/products/Type";
import {ProductCode} from "../../../models/products/ProductCode";
import {TranslateService} from "@ngx-translate/core";
import {ActivatedRoute, Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import Swal from "sweetalert2";
import {EUnit} from "../../../enums/EUnit";
import {TradeService} from "../../../services/trade.service";
import {UpdateOrderRequest} from "../../../models/trade/UpdateOrderRequest";

@Component({
  selector: 'app-add-order',
  templateUrl: './add-order.component.html',
  styleUrls: ['./add-order.component.sass'],
})
export class AddOrderComponent implements OnInit {

  formTitle = ""

  form!: FormGroup;
  isLoggedIn = false;
  hasPermissions = false;
  hide = true;

  units: Unit[] = [];
  types: Type[] = [];

  isProductSet = false;

  productList: ProductCode[] = [];
  unitsForProducts: string[] = [];

  isEditOrderView = false;
  orderId?: number;

  format = 'dd/MM/yyyy';
  locale = 'en-US';

  constructor(private formBuilder: FormBuilder, private tradeService: TradeService,
              private translate: TranslateService, private route: ActivatedRoute,
              private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      number: ['', Validators.required],
      date: [new Date(), Validators.required],
      discount: [''],
      delivery: [''],
      price: [''],
      name: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.pattern('(\\+48)?\\s?[0-9]{3}\\s?[0-9]{3}\\s?[0-9]{3}')],
      postalCode: ['', [Validators.required, Validators.pattern('[0-9]{2}-[0-9]{3}')]],
      post: ['', Validators.required],
      city: ['', Validators.required],
      street: ['', Validators.required],
      buildingNumber: ['', [Validators.required, Validators.pattern('[0-9]+.*')]],
      doorNumber: ['', Validators.pattern('[0-9]+.*')],
      productSet: this.formBuilder.array([])
    });
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    if(this.tokenStorage.getUser() && (this.tokenStorage.getUser().roles.includes(ERole[ERole.ROLE_ADMIN])
      || this.tokenStorage.getUser().roles.includes(ERole[ERole.ROLE_WAREHOUSE]))){
      this.hasPermissions = true;
    } else {
      this.router.navigate(['/profile']).then(() => this.reloadPage());
    }
    this.loadUnits();
    this.addProduct();
    this.loadProductForSetList();
    this.checkIfEditOrderView();
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

  get productSet(): FormArray {
    return this.form.controls['productSet'] as FormArray;
  }

  addProduct() {
    const products = this.form.controls['productSet'] as FormArray;
    products.push(this.formBuilder.group({
      product: [null, Validators.required],
      quantity: ['', Validators.required],
    }));
    this.unitsForProducts.push("");
  }

  deleteProducts(){
    const products = this.form.controls['productSet'] as FormArray;
    products.clear()
    this.unitsForProducts = [];
  }

  deleteProduct(i: number) {
    const products = this.form.controls['productSet'] as FormArray;
    products.removeAt(i)
    this.unitsForProducts = this.unitsForProducts.slice(i,i+1)
  }

  loadProductForSetList(){
    this.tradeService.loadProductList()
      .subscribe({
        next: (data) => {
          console.log(data);
          this.productList = data;
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("products.add-product.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }

  loadUnits(){
    this.units = [
      {
        unit: EUnit.PIECES,
        name: this.getTranslateMessage("products.unit-pieces"),
        shortcut: this.getTranslateMessage("products.unit-pieces-s")
      },
      {
        unit: EUnit.LITERS,
        name: this.getTranslateMessage("products.unit-l"),
        shortcut: this.getTranslateMessage("products.unit-l-s")
      },
      {
        unit: EUnit.KILOGRAMS,
        name: this.getTranslateMessage("products.unit-kg"),
        shortcut: this.getTranslateMessage("products.unit-kg-s")
      },
      {
        unit: EUnit.METERS,
        name: this.getTranslateMessage("products.unit-m"),
        shortcut: this.getTranslateMessage("products.unit-m-s")
      },
      {
        unit: EUnit.SQUARE_METERS,
        name: this.getTranslateMessage("products.unit-m2"),
        shortcut: this.getTranslateMessage("products.unit-m2-s")
      }];
  }

  changeUnitForProduct(i: number) {
    const code = this.productSet.get([i])!.get('product')?.value
    const productUnit = this.productList.find(product => product.code === code)!.unit as unknown as string;
    this.unitsForProducts[i] = this.units.find(unit => EUnit[unit.unit] === productUnit)!.shortcut
  }

  getUnit(i: number): String{
    return this.unitsForProducts[i];
  }

  onSubmit(): void {
    this.tradeService.addOrder({
      buildingNumber: this.form.get('buildingNumber')?.value,
      city: this.form.get('city')?.value,
      delivery: this.form.get('delivery')?.value,
      discount: this.form.get('discount')?.value,
      doorNumber: this.form.get('doorNumber')?.value,
      email: this.form.get('email')?.value,
      name: this.form.get('name')?.value,
      number: this.form.get('number')?.value,
      orderDate: this.form.get('date')?.value,
      phone: this.form.get('phone')?.value,
      post: this.form.get('post')?.value,
      postalCode: this.form.get('postalCode')?.value,
      price: this.form.get('price')?.value,
      productSet: this.form.get('productSet')?.value,
      street: this.form.get('street')?.value,
      surname: this.form.get('surname')?.value
    }).subscribe({
      next: (data) => {
        console.log(data);
        this.router.navigate(['/add-order']).then(() => this.showSuccess("trade.add-order.add-success"));
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("trade.add-order.add-error"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    });
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


  checkIfEditOrderView(){
    this.route.params
      .subscribe(
        params => {
          console.log(params);
          if (params['id'] ){
            this.isEditOrderView = true;
            this.orderId = params['id'];
            this.formTitle = this.getTranslateMessage("trade.add-order.edit-title")
            this.getOrder()
          } else {
            this.formTitle = this.getTranslateMessage("trade.add-order.add-title")
          }
        }
      );
  }

  getOrder() {
    this.tradeService.getOrder(this.orderId!).subscribe({
      next: (data) => {
        console.log(data)
        this.fillFormWithEditedOrder(data);
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("trade.add-order.load-error"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    })
  }

  fillFormWithEditedOrder(data: UpdateOrderRequest){
    this.form.get('buildingNumber')?.setValue(data.buildingNumber);
    this.form.get('city')?.setValue(data.city);
    this.form.get('delivery')?.setValue(data.delivery);
    this.form.get('discount')?.setValue(data.discount);
    this.form.get('doorNumber')?.setValue(data.doorNumber);
    this.form.get('email')?.setValue(data.email);
    this.form.get('name')?.setValue(data.name);
    this.form.get('number')?.setValue(data.number);
    this.form.get('date')?.setValue(data.orderDate);
    this.form.get('phone')?.setValue(data.phone);
    this.form.get('post')?.setValue(data.post);
    this.form.get('postalCode')?.setValue(data.postalCode);
    this.form.get('price')?.setValue(data.price);
    this.form.get('street')?.setValue(data.street);
    this.form.get('surname')?.setValue(data.surname);
    if(data.productSet) {
      const productUnit = this.productList.find(product => product.code === data.productSet![0].product)!.unit as unknown as string;
      this.unitsForProducts[0] = this.units.find(unit => EUnit[unit.unit] === productUnit)!.shortcut
      for (let i= 1; i < data.productSet.length; i++){
        this.addProduct();
        const productUnit = this.productList.find(product => product.code === data.productSet![i].product)!.unit as unknown as string;
        this.unitsForProducts[i] = this.units.find(unit => EUnit[unit.unit] === productUnit)!.shortcut
      }
    }
    this.form.get('productSet')?.setValue(data.productSet)
  }

  updateOrder(): void {
    this.tradeService.updateOrder({
      id: this.orderId!,
      buildingNumber: this.form.get('buildingNumber')?.value,
      city: this.form.get('city')?.value,
      delivery: this.form.get('delivery')?.value,
      discount: this.form.get('discount')?.value,
      doorNumber: this.form.get('doorNumber')?.value,
      email: this.form.get('email')?.value,
      name: this.form.get('name')?.value,
      number: this.form.get('number')?.value,
      orderDate: this.form.get('date')?.value,
      phone: this.form.get('phone')?.value,
      post: this.form.get('post')?.value,
      postalCode: this.form.get('postalCode')?.value,
      price: this.form.get('price')?.value,
      productSet: this.form.get('productSet')?.value,
      street: this.form.get('street')?.value,
      surname: this.form.get('surname')?.value
    }).subscribe({
      next: (data) => {
        console.log(data);
        this.router.navigate(['/browse-orders']).then(() => this.showSuccess("trade.add-order.update-success"));
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("trade.add-order.update-error"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    });
  }
}
