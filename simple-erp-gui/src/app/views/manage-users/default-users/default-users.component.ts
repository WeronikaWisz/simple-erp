import { Component, OnInit } from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {DefaultUser} from "../../../models/manage-users/DefaultUser";
import {ManageUsersService} from "../../../services/manage-users/manage-users.service";
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {PageEvent} from "@angular/material/paginator";
import Swal from "sweetalert2";
import {ETask} from "../../../enums/ETask";
import {MatDialog} from "@angular/material/dialog";
import {UpdateProfileDialogComponent} from "../profile/update-profile-dialog/update-profile-dialog.component";
import {ChangeDefaultUserDialogComponent} from "./change-default-user-dialog/change-default-user-dialog.component";
import {ERole} from "../../../enums/ERole";

@Component({
  selector: 'app-default-users',
  templateUrl: './default-users.component.html',
  styleUrls: ['./default-users.component.sass']
})
export class DefaultUsersComponent implements OnInit {

  isLoggedIn = false;
  isAdmin = false;

  displayedColumns = ['task', 'employee', 'actions'];
  dataSource: MatTableDataSource<DefaultUser> = new MatTableDataSource<DefaultUser>([]);

  users: DefaultUser[] = [];

  totalUsersLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  constructor(private manageUsersService: ManageUsersService, private translate: TranslateService,
              private tokenStorage: TokenStorageService, private router: Router, public dialog: MatDialog) { }

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
    this.loadDefaultUsers();
  }

  loadDefaultUsers(){
    this.manageUsersService.loadDefaultUsers(this.pageIndex, this.pageSize)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.users = data;
          this.dataSource.data = this.users;
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("manage-users.default-users.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }

  goToEditUser(row: DefaultUser): void{
      const dialogRef = this.dialog.open(ChangeDefaultUserDialogComponent, {
        maxWidth: '650px',
        data: {
          taskId: row.taskId,
          employeeId: row.employeeId,
          task: this.getTaskName(row.task),
          employee: row.employeeId
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        console.log(result);
        if(result){
          this.loadDefaultUsers();
        }
      });
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
    this.loadDefaultUsers();
  }

  reloadPage(): void {
    window.location.reload();
  }

  getTaskName(task: string): string {
    if(task === ETask[ETask.TASK_PURCHASE]){
      return this.getTranslateMessage("manage-users.default-users.task-purchase")
    } else if(task === ETask[ETask.TASK_SALE]){
      return this.getTranslateMessage("manage-users.default-users.task-sale")
    } else if(task === ETask[ETask.TASK_PRODUCTION]){
      return this.getTranslateMessage("manage-users.default-users.task-production")
    } else if(task === ETask[ETask.TASK_INTERNAL_RELEASE]){
      return this.getTranslateMessage("manage-users.default-users.task-internal-release")
    } else if(task === ETask[ETask.TASK_INTERNAL_ACCEPTANCE]){
      return this.getTranslateMessage("manage-users.default-users.task-internal-acceptance")
    } else if(task === ETask[ETask.TASK_EXTERNAL_RELEASE]){
      return this.getTranslateMessage("manage-users.default-users.task-external-release")
    } else if(task === ETask[ETask.TASK_EXTERNAL_ACCEPTANCE]){
      return this.getTranslateMessage("manage-users.default-users.task-external-acceptance")
    } else {
      return "";
    }
  }

}
