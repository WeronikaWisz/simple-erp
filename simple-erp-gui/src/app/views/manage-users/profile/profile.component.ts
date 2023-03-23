import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from '../../../services/token-storage.service';
import {Router} from "@angular/router";
import Swal from "sweetalert2";
import {ProfileData} from "../../../models/manage-users/ProfileData";
import {UsersService} from "../../../services/manage-users/users.service";
import {MatDialog} from "@angular/material/dialog";
import {UpdateProfileDialogComponent} from "./update-profile-dialog/update-profile-dialog.component";
import {ChangePasswordDialogComponent} from "./change-password-dialog/change-password-dialog.component";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.sass']
})
export class ProfileComponent implements OnInit {

  isLoggedIn = false;
  profileData?: ProfileData;

  constructor(private tokenStorage: TokenStorageService, private router: Router, private translate: TranslateService,
              private usersService: UsersService, public dialog: MatDialog) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.getUserProfileData()
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
  }

  getUserProfileData(){
    this.usersService.getUserProfileData().subscribe(
      data => {
        console.log(data)
        this.profileData = data;
      }, err => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("manage-users.profile.load-data-error"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    )
  }

  editProfile(){
    if(this.profileData) {
      const dialogRef = this.dialog.open(UpdateProfileDialogComponent, {
        maxWidth: '650px',
        data: {
          email: this.profileData.email,
          name: this.profileData.name,
          surname: this.profileData.surname,
          phone: this.profileData.phone
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        console.log(result);
        if(result){
          this.getUserProfileData();
        }
      });
    }
  }

  changePassword(){
    this.dialog.open(ChangePasswordDialogComponent, {
      maxWidth: '650px'
      });
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

}
