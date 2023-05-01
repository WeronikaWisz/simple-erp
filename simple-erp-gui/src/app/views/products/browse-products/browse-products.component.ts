import { Component, OnInit } from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {FormBuilder} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import Swal from "sweetalert2";
import {PageEvent} from "@angular/material/paginator";
import {ProductListItem} from "../../../models/products/ProductListItem";
import {ProductsService} from "../../../services/products.service";
import {EUnit} from "../../../enums/EUnit";
import {EType} from "../../../enums/EType";
import {ContractorDialogComponent} from "../browse-contractors/contractor-dialog/contractor-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {ProductionInfoDialogComponent} from "../../production/browse-production/production-info-dialog/production-info-dialog.component";

@Component({
  selector: 'app-browse-products',
  templateUrl: './browse-products.component.html',
  styleUrls: ['./browse-products.component.sass']
})
export class BrowseProductsComponent implements OnInit {


  isLoggedIn = false;
  isAdmin = false;

  displayedColumns = ['code', 'name', 'type', 'purchasePrice', 'salePrice', 'unit', 'actions'];
  dataSource: MatTableDataSource<ProductListItem> = new MatTableDataSource<ProductListItem>([]);

  products: ProductListItem[] = [];
  productCount: number = 0;

  totalProductsLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  emptySearchList = false;

  constructor(private formBuilder: FormBuilder, private productsService: ProductsService,
              private translate: TranslateService, public dialog: MatDialog,
              private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
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
    this.loadProducts();
  }

  loadProducts(){
    this.emptySearchList = false;
    this.productsService.loadProducts(this.pageIndex, this.pageSize)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.products = data.productsList;
          this.productCount = data.totalProductsLength
          this.totalProductsLength = data.totalProductsLength
          if (this.products.length == 0) {
            this.emptySearchList = true;
          }
          this.dataSource.data = this.products;
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("products.browse-product.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }

  deleteProduct(id: number, type: EType){
    this.productsService.deleteProduct(id, type)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.loadProducts()
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("products.browse-product.delete-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      });
  }

  goToEditProduct(id: number, type: EType): void{
    this.router.navigate(['/edit-product', type, id]);
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

  pageChanged(event: PageEvent) {
    console.log({ event });
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadProducts();
  }

  goToAddProduct(){
    this.router.navigate(['/add-product']);
  }

  getUnit(eUnit: EUnit): string{
    let unit = eUnit as unknown as string;
    if(unit === EUnit[EUnit.PIECES]){
      return this.getTranslateMessage("products.unit-pieces-s")
    }
    if(unit === EUnit[EUnit.LITERS]){
      return this.getTranslateMessage("products.unit-l-s")
    }
    if(unit === EUnit[EUnit.METERS]){
      return this.getTranslateMessage("products.unit-m-s")
    }
    if(unit === EUnit[EUnit.SQUARE_METERS]){
      return this.getTranslateMessage("products.unit-m2-s")
    }
    if(unit === EUnit[EUnit.KILOGRAMS]){
      return this.getTranslateMessage("products.unit-kg-s")
    }
    return ''
  }

  getType(eType: EType): string{
    let type = eType as unknown as string;
    if(type === EType[EType.BOUGHT]){
      return this.getTranslateMessage("products.type-bought")
    }
    if(type === EType[EType.SET]){
      return this.getTranslateMessage("products.type-set")
    }
    if(type === EType[EType.PRODUCED]){
      return this.getTranslateMessage("products.type-produced")
    }
    return ''
  }

  showPrice(price: string): string{
    if(price){
      return price + ' zł'
    } else {
      return ''
    }
  }

  goToContractorInfo(id: number) {
    this.dialog.open(ContractorDialogComponent, {
      maxWidth: '650px',
      data: {
        id: id,
        isFromTrade: false
      }
    });
  }

  goToProductInfo(id: number) {
    this.dialog.open(ProductionInfoDialogComponent, {
      maxWidth: '650px',
      data: {
        id: id,
        from: 'PRODUCTS'
      }
    });
  }

  isProductionType(eType: EType): boolean {
    let type = eType as unknown as string;
    return type === EType[EType.PRODUCED];
  }

}
