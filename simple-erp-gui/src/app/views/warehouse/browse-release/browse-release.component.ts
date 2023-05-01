import {Component, OnInit} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {EStatus} from "../../../enums/EStatus";
import {FormBuilder} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import Swal from "sweetalert2";
import {PageEvent} from "@angular/material/paginator";
import {AssignedUsersDialogComponent} from "../browse-delegated-tasks/assigned-users-dialog/assigned-users-dialog.component";
import {EDirection} from "../../../enums/EDirection";
import {WarehouseService} from "../../../services/warehouse.service";
import {ReleaseAcceptanceListItem} from "../../../models/warehouse/ReleaseAcceptanceListItem";
import {Direction} from "../../../models/warehouse/Direction";
import {ChangeUserReleaseDialogComponent} from "./change-user-release-dialog/change-user-release-dialog.component";
import {ReleaseInfoDialogComponent} from "./release-info-dialog/release-info-dialog.component";
import {ETask} from "../../../enums/ETask";

@Component({
  selector: 'app-browse-release',
  templateUrl: './browse-release.component.html',
  styleUrls: ['./browse-release.component.sass']
})
export class BrowseReleaseComponent implements OnInit {

  isLoggedIn = false;
  hasPermissions = false;

  displayedColumns = ['select', 'number', 'direction', 'association', 'purchaser', 'date', 'assigned-user', 'actions'];
  dataSource: MatTableDataSource<ReleaseAcceptanceListItem> = new MatTableDataSource<ReleaseAcceptanceListItem>([]);
  selection = new SelectionModel<ReleaseAcceptanceListItem>(true, []);

  tasks: ReleaseAcceptanceListItem[] = [];
  taskCount: number = 0;

  totalTasksLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  emptySearchList = false;
  selectedTabIndex = 0;

  taskStatus: EStatus = EStatus.WAITING;

  taskDirection?: EDirection;

  directions: Direction[] = []

  constructor(private formBuilder: FormBuilder, private warehouseService: WarehouseService,
              private translate: TranslateService, public dialog: MatDialog,
              private router: Router, private tokenStorage: TokenStorageService) {
    this.directions = [
      {
        direction: EDirection.INTERNAL,
        name: this.getTranslateMessage("supplies.browse-release.internal")
      },
      {
        direction: EDirection.EXTERNAL,
        name: this.getTranslateMessage("supplies.browse-release.external")
      }
    ]
  }

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
    this.loadReleases();
  }

  loadReleases(){
    this.emptySearchList = false;
    this.warehouseService.loadReleases(EStatus[this.taskStatus],
      this.taskDirection != undefined ? EDirection[this.taskDirection] : null!, this.pageIndex, this.pageSize)
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
            title: this.getTranslateMessage("supplies.browse-release.load-error"),
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

  changeAssignedUser(){
    const dialogRef = this.dialog.open(ChangeUserReleaseDialogComponent, {
      maxWidth: '650px',
      data: {
        taskIds: this.selection.selected.map(s => s.id),
        task: ETask.TASK_EXTERNAL_RELEASE
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      if(result){
        this.selection.clear()
        this.loadReleases();
      }
    });
  }

  pageChanged(event: PageEvent) {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadReleases();
  }

  onTabChange(event: any) {
    this.taskStatus = event.index;
    this.pageIndex = 0;
    this.selection.clear()
    this.loadReleases();
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
    this.dialog.open(ReleaseInfoDialogComponent, {
      maxWidth: '650px',
      data: {
        id: id,
        from: 'WAREHOUSE'
      }
    });
  }

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
    this.warehouseService.markReleaseAsDone(this.selection.selected.map(s => s.id))
      .subscribe({
        next: (data) => {
          console.log(data);
          this.selection.clear()
          this.loadReleases();
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("supplies.browse-release.mark-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      });
  }


  markAsInProgress() {
    this.warehouseService.markReleaseAsInProgress(this.selection.selected.map(s => s.id))
      .subscribe({
        next: (data) => {
          console.log(data);
          this.selection.clear()
          this.loadReleases();
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("supplies.browse-release.mark-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      });
  }

  isExternal(eDirection: EDirection): boolean {
    let direction = eDirection as unknown as string;
    return direction === EDirection[EDirection.EXTERNAL];
  }
}
