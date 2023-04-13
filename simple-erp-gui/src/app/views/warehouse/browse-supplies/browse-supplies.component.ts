import {Component, OnInit} from '@angular/core';
import {EType} from "../../../enums/EType";
import {EUnit} from "../../../enums/EUnit";
import {PageEvent} from "@angular/material/paginator";
import {MatTableDataSource} from "@angular/material/table";
import {FormBuilder} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import {SuppliesListItem} from "../../../models/warehouse/SuppliesListItem";
import Swal from "sweetalert2";
import {WarehouseService} from "../../../services/warehouse.service";
import {MatDialog} from "@angular/material/dialog";
import {UpdateSuppliesDialogComponent} from "./update-supplies-dialog/update-supplies-dialog.component";
import {DelegatePurchaseDialogComponent} from "./delegate-purchase-dialog/delegate-purchase-dialog.component";

@Component({
  selector: 'app-browse-supplies',
  templateUrl: './browse-supplies.component.html',
  styleUrls: ['./browse-supplies.component.sass']
})
export class BrowseSuppliesComponent implements OnInit {

  isLoggedIn = false;
  hasPermissions = false;

  displayedColumns = ['code', 'name', 'type', 'quantity', 'unit', 'message', 'actions'];
  dataSource: MatTableDataSource<SuppliesListItem> = new MatTableDataSource<SuppliesListItem>([]);

  supplies: SuppliesListItem[] = [];
  productCount: number = 0;

  totalProductsLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  emptySearchList = false;

  constructor(private formBuilder: FormBuilder, private suppliesService: WarehouseService,
              private translate: TranslateService, public dialog: MatDialog,
              private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
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
    this.loadSupplies();
  }

  loadSupplies(){
    this.emptySearchList = false;
    this.suppliesService.loadSupplies(this.pageIndex, this.pageSize)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.supplies = data.suppliesList
          this.productCount = data.totalProductsLength
          this.totalProductsLength = data.totalProductsLength
          if (this.supplies.length == 0) {
            this.emptySearchList = true;
          }
          this.dataSource.data = this.supplies;
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

  addPurchaseTask(supplies: SuppliesListItem) {
    const dialogRef = this.dialog.open(DelegatePurchaseDialogComponent, {
      maxWidth: '650px',
      data: {
        stockLevelId: supplies.id,
        code: supplies.code,
        name: supplies.name,
        unit: supplies.unit,
        stockQuantity: supplies.quantity
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      if(result){
        this.loadSupplies();
      }
    });
  }

  // TODO
  addProductionTask(id: number) {

  }

  isProductionType(eType: EType): boolean {
    let type = eType as unknown as string;
    return type === EType[EType.PRODUCED];
  }

  isBoughtType(eType: EType): boolean {
    let type = eType as unknown as string;
    return type === EType[EType.BOUGHT];
  }

  goToEditSupplies(supplies: SuppliesListItem) {
      const dialogRef = this.dialog.open(UpdateSuppliesDialogComponent, {
        maxWidth: '650px',
        data: {
          id: supplies.id,
          code: supplies.code,
          name: supplies.name,
          unit: supplies.unit,
          quantity: supplies.quantity,
          minQuantity: supplies.minQuantity,
          days: supplies.days
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        console.log(result);
        if(result){
          this.loadSupplies();
        }
      });
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
    this.loadSupplies();
  }

  isLessThatMin(element: SuppliesListItem): boolean {
    return +element.quantity < +element.minQuantity;
  }
}
