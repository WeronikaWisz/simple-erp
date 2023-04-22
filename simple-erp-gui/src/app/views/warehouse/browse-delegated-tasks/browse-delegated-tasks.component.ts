import {Component, OnInit} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {DelegatedTaskListItem} from "../../../models/warehouse/DelegatedTaskListItem";
import {EStatus} from "../../../enums/EStatus";
import {EUnit} from "../../../enums/EUnit";
import {PageEvent} from "@angular/material/paginator";
import {FormBuilder} from "@angular/forms";
import {WarehouseService} from "../../../services/warehouse.service";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import {EType} from "../../../enums/EType";
import Swal from "sweetalert2";
import {DelegatePurchaseDialogComponent} from "../browse-supplies/delegate-purchase-dialog/delegate-purchase-dialog.component";
import {AssignedUsersDialogComponent} from "./assigned-users-dialog/assigned-users-dialog.component";

@Component({
  selector: 'app-browse-delegated-tasks',
  templateUrl: './browse-delegated-tasks.component.html',
  styleUrls: ['./browse-delegated-tasks.component.sass']
})
export class BrowseDelegatedTasksComponent implements OnInit {

  isLoggedIn = false;
  hasPermissions = false;

  displayedColumns = ['number', 'code', 'name', 'quantity', 'unit', 'status', 'purchaser', 'date', 'assigned-user', 'actions'];
  dataSource: MatTableDataSource<DelegatedTaskListItem> = new MatTableDataSource<DelegatedTaskListItem>([]);

  tasks: DelegatedTaskListItem[] = [];
  taskCount: number = 0;

  totalTasksLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  emptySearchList = false;
  selectedTabIndex = 0;

  taskType: EType = EType.BOUGHT;

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
    this.loadDelegatedTasks();
  }

  loadDelegatedTasks(){
    this.emptySearchList = false;
    this.suppliesService.loadDelegatedTasks(EType[this.taskType], this.pageIndex, this.pageSize)
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
            title: this.getTranslateMessage("supplies.delegated-tasks.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }

  goToEditTask(row: DelegatedTaskListItem) {
    const dialogRef = this.dialog.open(DelegatePurchaseDialogComponent, {
      maxWidth: '650px',
      data: {
        taskType: this.taskType,
        taskId: row.id,
        code: row.code,
        name: row.name,
        unit: row.unit,
        quantity: row.quantity,
        stockQuantity: row.stockQuantity
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      if(result){
        this.loadDelegatedTasks();
      }
    });
  }

  deleteTask(id: number) {
    this.suppliesService.deleteTask(id, EType[this.taskType])
      .subscribe({
        next: (data) => {
          console.log(data);
          this.loadDelegatedTasks();
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("supplies.delegated-tasks.delete-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      });
  }

  getStatus(eStatus: EStatus): string {
    let status = eStatus as unknown as string;
    if(status === EStatus[EStatus.WAITING]) {
      return this.getTranslateMessage("tasks.waiting")
    }
    if(status === EStatus[EStatus.IN_PROGRESS]){
      return this.getTranslateMessage("tasks.in-progress")
    }
    return '';
  }

  getUnit(eUnit: EUnit): string {
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

  pageChanged(event: PageEvent) {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadDelegatedTasks();
  }

  onTabChange(event: any) {
    this.taskType = event.index;
    this.pageIndex = 0;
    this.loadDelegatedTasks();
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
}
