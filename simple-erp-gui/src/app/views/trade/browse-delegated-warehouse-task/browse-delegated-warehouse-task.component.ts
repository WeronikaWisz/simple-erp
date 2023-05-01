import {Component, OnInit} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {ReleaseAcceptanceListItem} from "../../../models/warehouse/ReleaseAcceptanceListItem";
import {FormBuilder} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {ActivatedRoute, Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import {ETask} from "../../../enums/ETask";
import {PageEvent} from "@angular/material/paginator";
import {TradeService} from "../../../services/trade.service";
import {EStatus} from "../../../enums/EStatus";
import {ReleaseInfoDialogComponent} from "../../warehouse/browse-release/release-info-dialog/release-info-dialog.component";
import {AssignedUsersDialogComponent} from "../../warehouse/browse-delegated-tasks/assigned-users-dialog/assigned-users-dialog.component";
import Swal from "sweetalert2";
import {AcceptanceInfoDialogComponent} from "../../warehouse/browse-acceptance/acceptance-info-dialog/acceptance-info-dialog.component";
import {ProductionService} from "../../../services/production.service";

@Component({
  selector: 'app-browse-delegated-warehouse-task',
  templateUrl: './browse-delegated-warehouse-task.component.html',
  styleUrls: ['./browse-delegated-warehouse-task.component.sass']
})
export class BrowseDelegatedWarehouseTaskComponent implements OnInit {

  isLoggedIn = false;
  hasPermissions = false;

  displayedColumns = ['number', 'association', 'purchaser', 'date', 'status', 'assigned-user', 'actions'];
  dataSource: MatTableDataSource<ReleaseAcceptanceListItem> = new MatTableDataSource<ReleaseAcceptanceListItem>([]);

  tasks: ReleaseAcceptanceListItem[] = [];
  taskCount: number = 0;

  totalTasksLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  emptySearchList = false;
  selectedTabIndex = 0;

  taskType: ETask = ETask.TASK_EXTERNAL_RELEASE;

  moduleName = 'TRADE'

  constructor(private formBuilder: FormBuilder, private tradeService: TradeService,
              private translate: TranslateService, public dialog: MatDialog,
              private router: Router, private tokenStorage: TokenStorageService,
              private route: ActivatedRoute, private productionService: ProductionService) {
  }

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
    this.checkIfTradeOrProduction()
  }

  checkIfTradeOrProduction(){
    this.route.params
      .subscribe(
        params => {
          console.log(params);
          if (params['type']){
            this.moduleName = params['type'].toUpperCase()
            if(this.moduleName === 'PRODUCTION'){
              this.taskType = ETask.TASK_INTERNAL_RELEASE
            } else {
              this.taskType = ETask.TASK_EXTERNAL_RELEASE
            }
          }
          this.selectedTabIndex = 0;
          this.loadTasks()
        }
      );
  }

  loadTasks(){
    this.emptySearchList = false;
    if(this.moduleName === 'TRADE') {
      this.tradeService.loadDelegatedTasks(ETask[this.taskType], this.pageIndex, this.pageSize)
        .subscribe({
          next: (data) => {
            console.log(data);
            this.tasks = data.releasesList
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
              title: this.getTranslateMessage("trade.browse-delegated-warehouse-task.load-error"),
              text: err.error.message,
              icon: 'error',
              showConfirmButton: false
            })
          }
        })
    } else {
      this.productionService.loadDelegatedTasks(ETask[this.taskType], this.pageIndex, this.pageSize)
        .subscribe({
          next: (data) => {
            console.log(data);
            this.tasks = data.releasesList
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
              title: this.getTranslateMessage("trade.browse-delegated-warehouse-task.load-error"),
              text: err.error.message,
              icon: 'error',
              showConfirmButton: false
            })
          }
        })
    }
  }

  pageChanged(event: PageEvent) {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadTasks();
  }

  onTabChange(event: any) {
    if(event.index === 0) {
      if(this.moduleName === 'TRADE') {
        this.taskType = ETask.TASK_EXTERNAL_RELEASE
      } else {
        this.taskType = ETask.TASK_INTERNAL_RELEASE
      }
    } else {
      if(this.moduleName === 'TRADE') {
        this.taskType = ETask.TASK_EXTERNAL_ACCEPTANCE
      } else {
        this.taskType = ETask.TASK_INTERNAL_ACCEPTANCE
      }
    }
    this.pageIndex = 0;
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

  goToTaskInfo(id: number) {
    if (this.taskType === ETask.TASK_EXTERNAL_RELEASE || this.taskType === ETask.TASK_INTERNAL_RELEASE) {
      this.dialog.open(ReleaseInfoDialogComponent, {
        maxWidth: '650px',
        data: {
          id: id,
          from: this.moduleName
        }
      });
    } else {
      this.dialog.open(AcceptanceInfoDialogComponent, {
        maxWidth: '650px',
        data: {
          id: id,
          from: this.moduleName
        }
      });
    }
  }

  isWaiting(eStatus: EStatus): boolean {
    let status = eStatus as unknown as string;
    return status === EStatus[EStatus.WAITING];
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
}
