import { Component, OnInit } from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {PageEvent} from "@angular/material/paginator";
import Swal from "sweetalert2";
import {FormBuilder} from "@angular/forms";
import {ManageUsersService} from "../../../services/manage-users/manage-users.service";
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {UserListItem} from "../../../models/manage-users/UserListItem";
import {ERole} from "../../../enums/ERole";

@Component({
  selector: 'app-browse-users',
  templateUrl: './browse-users.component.html',
  styleUrls: ['./browse-users.component.sass']
})
export class BrowseUsersComponent implements OnInit {

  isLoggedIn = false;
  isAdmin = false;

  displayedColumns = ['username', 'name', 'surname', 'email', 'phone', 'roles', 'actions'];
  dataSource: MatTableDataSource<UserListItem> = new MatTableDataSource<UserListItem>([]);

  users: UserListItem[] = [];
  userCount: number = 0;

  totalUsersLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  emptySearchList = false;

  constructor(private formBuilder: FormBuilder, private manageUsersService: ManageUsersService,
              private translate: TranslateService,
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
    this.loadUsers();
  }

  loadUsers(){
    this.emptySearchList = false;
    this.manageUsersService.loadUsers( this.pageIndex, this.pageSize)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.users = data.userList;
          this.userCount = data.totalUsersLength;
          this.totalUsersLength = data.totalUsersLength;
          if (this.users.length == 0) {
            this.emptySearchList = true;
          }
          this.dataSource.data = this.users;
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("manage-users.browse-users.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }

  deleteUser(id: number){
    this.manageUsersService.deleteUser(id)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.loadUsers()
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("manage-users.browse-users.delete-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      });
  }

  goToEditUser(id: number): void{
    this.router.navigate(['/edit-user', id]);
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
    this.loadUsers();
  }

  getRoleName(role: string): string {
    if(role === ERole[ERole.ROLE_WAREHOUSE]){
      return this.getTranslateMessage("manage-users.register.role-warehouse")
    } else if (role === ERole[ERole.ROLE_PRODUCTION]){
      return this.getTranslateMessage("manage-users.register.role-production")
    } else if (role === ERole[ERole.ROLE_TRADE]){
      return this.getTranslateMessage("manage-users.register.role-trade")
    } else if (role === ERole[ERole.ROLE_ADMIN]){
      return this.getTranslateMessage("manage-users.register.role-admin")
    } else {
      return "";
    }
  }

  isNotAdmin(roles: string[]): boolean{
    return !roles.includes(ERole[ERole.ROLE_ADMIN]);
  }

  goToAddUser(){
    this.router.navigate(['/add-user']);
  }

}
