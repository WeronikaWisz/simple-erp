import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./views/manage-users/login/login.component";
import {ProfileComponent} from "./views/manage-users/profile/profile.component";
import {AddUserComponent} from "./views/manage-users/add-user/add-user.component";
import {BrowseUsersComponent} from "./views/manage-users/browse-users/browse-users.component";

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'add-user', component: AddUserComponent },
  { path: 'edit-user/:id', component: AddUserComponent },
  { path: 'browse-users', component: BrowseUsersComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
