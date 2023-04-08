import {Component, OnInit} from '@angular/core';
import {SelectionModel} from "@angular/cdk/collections";
import {PageEvent} from "@angular/material/paginator";
import {EStatus} from "../../../enums/EStatus";
import {AssignedUsersDialogComponent} from "../../warehouse/browse-delegated-tasks/assigned-users-dialog/assigned-users-dialog.component";
import {MatTableDataSource} from "@angular/material/table";
import {FormBuilder} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import {OrderListItem} from "../../../models/trade/OrderListItem";
import {TradeService} from "../../../services/trade.service";
import Swal from "sweetalert2";
import {CustomerDialogComponent} from "./customer-dialog/customer-dialog.component";
import {ChangeDefaultUserDialogComponent} from "../../manage-users/default-users/change-default-user-dialog/change-default-user-dialog.component";
import {ChangeUserDialogComponent} from "./change-user-dialog/change-user-dialog.component";

@Component({
  selector: 'app-browse-orders',
  templateUrl: './browse-orders.component.html',
  styleUrls: ['./browse-orders.component.sass']
})
export class BrowseOrdersComponent implements OnInit {

  isLoggedIn = false;
  hasPermissions = false;

  displayedColumns = ['select', 'number', 'customer', 'date', 'price', 'assigned-user', 'actions'];
  dataSource: MatTableDataSource<OrderListItem> = new MatTableDataSource<OrderListItem>([]);
  selection = new SelectionModel<OrderListItem>(true, []);

  orders: OrderListItem[] = [];
  orderCount: number = 0;

  totalOrdersLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  emptySearchList = false;
  selectedTabIndex = 0;

  orderStatus: EStatus = EStatus.WAITING;

  constructor(private formBuilder: FormBuilder, private tradeService: TradeService,
              private translate: TranslateService, public dialog: MatDialog,
              private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    if(this.tokenStorage.getUser() && (this.tokenStorage.getUser().roles.includes(ERole[ERole.ROLE_ADMIN])
      || this.tokenStorage.getUser().roles.includes(ERole[ERole.ROLE_TRADE]))){
      this.hasPermissions = true;
    } else {
      this.router.navigate(['/profile']).then(() => this.reloadPage());
    }
    this.loadOrders();
  }

  loadOrders(){
    this.emptySearchList = false;
    this.tradeService.loadOrders(EStatus[this.orderStatus], this.pageIndex, this.pageSize)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.orders = data.ordersList
          this.orderCount = data.totalOrdersLength
          this.totalOrdersLength = data.totalOrdersLength
          if (this.orders.length == 0) {
            this.emptySearchList = true;
          }
          this.dataSource.data = this.orders;
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("trade.browse-orders.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(row => this.selection.select(row));
  }

  delegateExternalRelease() {
    this.selection.selected.forEach(s => console.log(s.id));
  }

  changeAssignedUser(){
    const dialogRef = this.dialog.open(ChangeUserDialogComponent, {
      maxWidth: '650px',
      data: this.selection.selected.map(s => s.id)
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      if(result){
        this.selection.clear()
        this.loadOrders();
      }
    });
  }

  pageChanged(event: PageEvent) {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadOrders();
  }

  onTabChange(event: any) {
    this.orderStatus = event.index;
    this.pageIndex = 0;
    this.loadOrders();
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

  isWaiting(eStatus: EStatus): boolean {
    let status = eStatus as unknown as string;
    return status === EStatus[EStatus.WAITING];
  }

  showUserData(assignedUserId: any) {
    this.dialog.open(AssignedUsersDialogComponent, {
      maxWidth: '650px',
      data: assignedUserId
    });
  }

  goToEditOrder(id: number) {
    this.router.navigate(['/edit-order', id]);
  }

  goToOrderInfo(id: number) {

  }

  deleteOrder(id: number) {
    this.tradeService.deleteOrder(id)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.loadOrders();
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("trade.browse-orders.delete-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      });
  }

  showCustomerData(id: number) {
    this.dialog.open(CustomerDialogComponent, {
      maxWidth: '650px',
      data: id
    });
  }

  isStatusWaiting(): boolean {
    return this.orderStatus === EStatus.WAITING;
  }

  isNothingSelected(): boolean {
    return this.selection.selected.length === 0;
  }
}
