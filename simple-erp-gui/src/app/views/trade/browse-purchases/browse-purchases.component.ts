import { Component, OnInit } from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {EStatus} from "../../../enums/EStatus";
import {FormBuilder} from "@angular/forms";
import {TradeService} from "../../../services/trade.service";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import Swal from "sweetalert2";
import {ChangeUserDialogComponent} from "../browse-orders/change-user-dialog/change-user-dialog.component";
import {PageEvent} from "@angular/material/paginator";
import {AssignedUsersDialogComponent} from "../../warehouse/browse-delegated-tasks/assigned-users-dialog/assigned-users-dialog.component";
import {DelegatedTaskListItem} from "../../../models/warehouse/DelegatedTaskListItem";
import {EUnit} from "../../../enums/EUnit";
import {ETask} from "../../../enums/ETask";
import {PurchaseOrderNumberDialogComponent} from "./purchase-order-number-dialog/purchase-order-number-dialog.component";

@Component({
  selector: 'app-browse-purchases',
  templateUrl: './browse-purchases.component.html',
  styleUrls: ['./browse-purchases.component.sass']
})
export class BrowsePurchasesComponent implements OnInit {


  isLoggedIn = false;
  hasPermissions = false;

  displayedColumns = ['select', 'number', 'code', 'name', 'quantity', 'unit', 'purchaser', 'date', 'assigned-user', 'message'];
  dataSource: MatTableDataSource<DelegatedTaskListItem> = new MatTableDataSource<DelegatedTaskListItem>([]);
  selection = new SelectionModel<DelegatedTaskListItem>(true, []);

  tasks: DelegatedTaskListItem[] = [];
  taskCount: number = 0;

  totalTasksLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  emptySearchList = false;
  selectedTabIndex = 0;

  taskStatus: EStatus = EStatus.WAITING;

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
    this.loadTasks();
  }

  loadTasks(){
    this.emptySearchList = false;
    this.tradeService.loadPurchaseTasks(EStatus[this.taskStatus], this.pageIndex, this.pageSize)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.tasks = data.tasksList
          this.taskCount = data.totalTasksLength
          this.totalTasksLength = data.totalTasksLength
          if (this.tasks.length == 0) {
            this.emptySearchList = true;
          }
          this.dataSource.data = this.tasks;
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("trade.browse-purchases.load-error"),
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

  delegateExternalAcceptance() {
    const dialogRef = this.dialog.open(PurchaseOrderNumberDialogComponent, {
      maxWidth: '650px',
      data: {
        ids: this.selection.selected.map(s => s.id),
        orderNumber: undefined
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      if(result){
        this.selection.clear();
        this.loadTasks();
      }
    });
  }

  changeAssignedUser(){
    const dialogRef = this.dialog.open(ChangeUserDialogComponent, {
      maxWidth: '650px',
      data: {
        taskIds: this.selection.selected.map(s => s.id),
        task: ETask.TASK_PURCHASE
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      if(result){
        this.selection.clear()
        this.loadTasks();
      }
    });
  }

  pageChanged(event: PageEvent) {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadTasks();
  }

  onTabChange(event: any) {
    this.taskStatus = event.index;
    this.pageIndex = 0;
    this.selection.clear()
    this.loadTasks();
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

  showUserData(assignedUserId: any) {
    this.dialog.open(AssignedUsersDialogComponent, {
      maxWidth: '650px',
      data: assignedUserId
    });
  }

  // showCustomerData(id: number) {
  //   this.dialog.open(CustomerDialogComponent, {
  //     maxWidth: '650px',
  //     data: id
  //   });
  // }

  isStatusWaiting(): boolean {
    return this.taskStatus === EStatus.WAITING;
  }

  isStatusNotDone(): boolean {
    return this.taskStatus !== EStatus.DONE;
  }

  isNothingSelected(): boolean {
    return this.selection.selected.length === 0;
  }

  isStatusInProgress() {
    return this.taskStatus === EStatus.IN_PROGRESS;
  }

  markAsDone() {
    this.tradeService.markPurchaseAsDone(this.selection.selected.map(s => s.id))
      .subscribe({
        next: (data) => {
          console.log(data);
          this.selection.clear()
          this.loadTasks()
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("trade.browse-purchases.mark-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      });
  }

  selectedAreNotAccepted(): boolean {
    return this.selection.selected.find(s => !s.accepted) != undefined;
  }

  selectedAreAccepted(): boolean {
    return this.selection.selected.find(s => s.accepted || s.orderNumber) != undefined;
  }

  markAsInProgress() {
    this.tradeService.markPurchaseAsInProgress(this.selection.selected.map(s => s.id))
      .subscribe({
        next: (data) => {
          console.log(data);
          this.selection.clear()
          this.loadTasks();
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("trade.browse-purchases.mark-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
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

  editOrderNumber(delegatedTaskListItem: DelegatedTaskListItem) {
    const dialogRef = this.dialog.open(PurchaseOrderNumberDialogComponent, {
      maxWidth: '650px',
      data: {
        ids: [delegatedTaskListItem.id],
        orderNumber: delegatedTaskListItem.orderNumber
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      if(result){
        this.selection.clear();
        this.loadTasks();
      }
    });
  }
}
