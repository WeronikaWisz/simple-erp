import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";
import {ActivatedRoute, Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import Swal from "sweetalert2";
import {Unit} from "../../../models/products/Unit";
import {Type} from "../../../models/products/Type";
import {EUnit} from "../../../enums/EUnit";
import {EType} from "../../../enums/EType";
import {ProductCode} from "../../../models/products/ProductCode";
import {ProductsService} from "../../../services/products.service";
import {ProductListItem} from "../../../models/products/ProductListItem";
import {ContractorName} from "../../../models/products/ContractorName";
import {UpdateProductRequest} from "../../../models/products/UpdateProductRequest";

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

  units: Unit[] = [];
  types: Type[] = [];

  isProductSet = false;
  isProducedType = false;
  isBoughtType = false;

  productList: ProductCode[] = [];
  unitsForProducts: string[] = [];

  contractors: ContractorName[] = [];

  isEditProductView = false;
  productId?: number;
  productType?: EType;

  constructor(private formBuilder: FormBuilder, private productsService: ProductsService,
              private translate: TranslateService, private route: ActivatedRoute,
              private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      code: ['', Validators.required],
      name: ['', Validators.required],
      type: [null, Validators.required],
      unit: [null, Validators.required],
      purchasePrice: ['', Validators.required],
      salePrice: ['', Validators.required],
      contractor: [null],
      productSet: this.formBuilder.array([]),
      productionSteps: this.formBuilder.array([])
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
    this.loadUnits();
    this.loadTypes();
    this.loadProductForSetList();
    this.loadContractors();
    this.checkIfEditProductView();
  }

  get productionSteps(): FormArray {
    return this.form.controls['productionSteps'] as FormArray;
  }

  addProductionStep() {
    const productionSteps = this.form.controls['productionSteps'] as FormArray;
    productionSteps.push(this.formBuilder.group({
      description: ['', Validators.required]
    }));
  }

  deleteProductionSteps(){
    const productionSteps = this.form.controls['productionSteps'] as FormArray;
    productionSteps.clear()
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

  loadProductForSetList(){
    this.productsService.loadProductForSetList()
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

  loadTypes(){
    this.types = [
      {
        type: EType.BOUGHT,
        name: this.getTranslateMessage("products.type-bought")
      },
      {
        type: EType.PRODUCED,
        name: this.getTranslateMessage("products.type-produced")
      },
      {
        type: EType.SET,
        name: this.getTranslateMessage("products.type-set")
      },
      ];
  }

  typeChange(){
    if(this.form.get('type')?.value === EType.BOUGHT){
      this.form.get('unit')?.enable();
      this.form.get('purchasePrice')?.enable();
      this.form.get('purchasePrice')?.addValidators(Validators.required);
      this.form.get('salePrice')?.clearValidators();
      this.isProductSet = false;
      this.isBoughtType = true;
      this.isProducedType = false;
      this.deleteProducts();
      this.deleteProductionSteps();
    } else if (this.form.get('type')?.value === EType.PRODUCED){
      this.form.get('unit')?.enable();
      this.form.get('purchasePrice')?.clearValidators();
      this.form.get('purchasePrice')?.disable();
      this.form.get('purchasePrice')?.setValue('');
      this.form.get('salePrice')?.addValidators(Validators.required);
      this.isProductSet = false;
      this.isBoughtType = false;
      this.isProducedType = true;
      this.deleteProducts();
      this.addProduct();
      this.addProductionStep();
    } else {
      this.form.get('unit')?.disable();
      this.form.get('unit')?.setValue(EUnit.PIECES);
      this.form.get('purchasePrice')?.clearValidators();
      this.form.get('purchasePrice')?.disable();
      this.form.get('purchasePrice')?.setValue('');
      this.form.get('salePrice')?.addValidators(Validators.required);
      this.isProductSet = true;
      this.isBoughtType = false;
      this.isProducedType = false;
      this.deleteProductionSteps();
      this.deleteProducts();
      this.addProduct();
    }
    this.form.get('productSet')?.updateValueAndValidity();
    this.form.get('productionSteps')?.updateValueAndValidity();
    this.form.get('purchasePrice')?.updateValueAndValidity();
    this.form.get('salePrice')?.updateValueAndValidity();
  }

  onSubmit(): void {
    this.productsService.addProduct({
      code: this.form.get('code')?.value,
      name: this.form.get('name')?.value,
      productSet: this.form.get('productSet')?.value,
      purchasePrice: this.form.get('purchasePrice')?.value,
      salePrice: this.form.get('salePrice')?.value,
      type: this.form.get('type')?.value,
      unit: this.form.get('unit')?.value,
      contractor: this.form.get('contractor')?.value,
      productionSteps: this.form.get('productionSteps')?.value
    }).subscribe({
      next: (data) => {
        console.log(data);
        this.router.navigate(['/add-product']).then(() => this.showSuccess("products.add-product.add-success"));
      },
      error: (err) => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("products.add-product.add-error"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    });
  }

  changeUnitForProduct(i: number) {
    const id = this.productSet.get([i])!.get('product')?.value
    const productUnit = this.productList.find(product => product.id === id)!.unit as unknown as string;
    this.unitsForProducts[i] = this.units.find(unit => EUnit[unit.unit] === productUnit)!.shortcut
  }

  getUnit(i: number): String{
    return this.unitsForProducts[i];
  }

  reloadPage(): void {
    window.location.reload();
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

  getTranslateMessage(key: string): string{
    let message = "";
    this.translate.get(key).subscribe(data =>
      message = data
    );
    return message;
  }

  checkIfEditProductView(){
    this.route.params
      .subscribe(
        params => {
          console.log(params);
          if (params['id'] && params['type']){
            this.isEditProductView = true;
            this.productId = params['id'];
            this.productType = params['type']
            this.form.get('type')?.disable();
            this.formTitle = this.getTranslateMessage("products.add-product.edit-title")
            this.getProduct()
          } else {
            this.formTitle = this.getTranslateMessage("products.add-product.product-title")
          }
        }
      );
  }

  getProduct() {
    this.productsService.getProduct(this.productId!, this.productType!).subscribe({
      next: (data) => {
        console.log(data)
        this.fillFormWithEditedProduct(data);
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

  fillFormWithEditedProduct(data: UpdateProductRequest){
    this.form.get('type')?.setValue(EType[data.type])
    this.typeChange()
    this.form.get('code')?.setValue(data.code)
    this.form.get('name')?.setValue(data.name)
    this.form.get('purchasePrice')?.setValue(data.purchasePrice)
    this.form.get('salePrice')?.setValue(data.salePrice)
    this.form.get('unit')?.setValue(EUnit[data.unit])
    this.form.get('contractor')?.setValue(data.contractor)
    if(data.productSet) {
      const productUnit = this.productList.find(product => product.id === data.productSet![0].product)!.unit as unknown as string;
      this.unitsForProducts[0] = this.units.find(unit => EUnit[unit.unit] === productUnit)!.shortcut
      for (let i= 1; i < data.productSet.length; i++){
        this.addProduct();
        const productUnit = this.productList.find(product => product.id === data.productSet![i].product)!.unit as unknown as string;
        this.unitsForProducts[i] = this.units.find(unit => EUnit[unit.unit] === productUnit)!.shortcut
      }
    }
    this.form.get('productSet')?.setValue(data.productSet)
    if(data.productionSteps){
      for (let i= 1; i < data.productionSteps.length; i++){
        this.addProductionStep();
      }
    }
    this.form.get('productionSteps')?.setValue(data.productionSteps)
  }

  updateProduct(): void {
      this.productsService.updateProduct({
        id: this.productId!,
        code: this.form.get('code')?.value,
        name: this.form.get('name')?.value,
        productSet: this.form.get('productSet')?.value,
        purchasePrice: this.form.get('purchasePrice')?.value,
        salePrice: this.form.get('salePrice')?.value,
        type: this.form.get('type')?.value,
        unit: this.form.get('unit')?.value,
        contractor: this.form.get('contractor')?.value,
        productionSteps: this.form.get('productionSteps')?.value
      }).subscribe({
        next: (data) => {
          console.log(data);
          this.router.navigate(['/browse-products']).then(() => this.showSuccess("products.add-product.update-success"));
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("products.add-product.update-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      });
  }

  deleteProduct(i: number) {
    const products = this.form.controls['productSet'] as FormArray;
    products.removeAt(i)
    this.unitsForProducts = this.unitsForProducts.slice(i,i+1)
  }

  deleteProductionStep(i: number) {
    const productionSteps = this.form.controls['productionSteps'] as FormArray;
    productionSteps.removeAt(i)
  }

  private loadContractors() {
    this.productsService.loadContractorsNames()
      .subscribe({
        next: (data) => {
          console.log(data);
          this.contractors = data;
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("products.add-product.load-error-2"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }
}
