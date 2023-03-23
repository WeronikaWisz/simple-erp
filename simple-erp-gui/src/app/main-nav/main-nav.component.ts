import { Component, OnInit } from '@angular/core';
import {TokenStorageService} from "../services/token-storage.service";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {ChangePasswordDialogComponent} from "../views/manage-users/profile/change-password-dialog/change-password-dialog.component";

@Component({
  selector: 'app-main-nav',
  templateUrl: './main-nav.component.html',
  styleUrls: ['./main-nav.component.sass']
})
export class MainNavComponent implements OnInit {

  private roles: string[] = [];
  isLoggedIn = false;
  username?: string;

  constructor(private tokenStorageService: TokenStorageService, public translate: TranslateService,
              public dialog: MatDialog, private router: Router) { }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();

    if (this.isLoggedIn) {
      const user = this.tokenStorageService.getUser();
      this.roles = user.roles;

      this.username = user.username;
    }
  }

  logout(): void {
    this.tokenStorageService.signOut();
    this.router.navigate(['/login']).then(() => this.reloadPage());
  }

  changePassword(){
    this.dialog.open(ChangePasswordDialogComponent, {
      maxWidth: '650px'
    });
  }


  reloadPage(): void {
    window.location.reload();
  }

}
