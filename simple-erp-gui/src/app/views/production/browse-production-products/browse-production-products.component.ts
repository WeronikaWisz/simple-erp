import { Component, OnInit } from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {FormBuilder} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import Swal from "sweetalert2";
import {PageEvent} from "@angular/material/paginator";
import {EUnit} from "../../../enums/EUnit";
import {ProductionInfoDialogComponent} from "../browse-production/production-info-dialog/production-info-dialog.component";
import {ProductionService} from "../../../services/production.service";
import {ProductionProductListItem} from "../../../models/production/ProductionProductListItem";

@Component({
  selector: 'app-browse-production-products',
  templateUrl: './browse-production-products.component.html',
  styleUrls: ['./browse-production-products.component.sass']
})
export class BrowseProductionProductsComponent implements OnInit {

  isLoggedIn = false;
  hasPermissions = false;

  displayedColumns = ['code', 'name', 'unit', 'actions'];
  dataSource: MatTableDataSource<ProductionProductListItem> = new MatTableDataSource<ProductionProductListItem>([]);

  products: ProductionProductListItem[] = [];
  productCount: number = 0;

  totalProductsLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  emptySearchList = false;

  constructor(private formBuilder: FormBuilder, private productionService: ProductionService,
              private translate: TranslateService, public dialog: MatDialog,
              private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    if(this.tokenStorage.getUser() && (this.tokenStorage.getUser().roles.includes(ERole[ERole.ROLE_ADMIN])
      || this.tokenStorage.getUser().roles.includes(ERole[ERole.ROLE_PRODUCTION]))){
      this.hasPermissions = true;
    } else {
      this.router.navigate(['/profile']).then(() => this.reloadPage());
    }
    this.loadProducts();
  }

  loadProducts(){
    this.emptySearchList = false;
    this.productionService.loadProductionProducts(this.pageIndex, this.pageSize)
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

  goToProductInfo(id: number) {
    this.dialog.open(ProductionInfoDialogComponent, {
      maxWidth: '650px',
      data: {
        id: id,
        from: 'PRODUCTION-PRODUCT'
      }
    });
  }

}
