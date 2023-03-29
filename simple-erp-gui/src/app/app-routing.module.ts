import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./views/manage-users/login/login.component";
import {ProfileComponent} from "./views/manage-users/profile/profile.component";
import {AddUserComponent} from "./views/manage-users/add-user/add-user.component";
import {BrowseUsersComponent} from "./views/manage-users/browse-users/browse-users.component";
import {DefaultUsersComponent} from "./views/manage-users/default-users/default-users.component";
import {AddProductComponent} from "./views/products/add-product/add-product.component";
import {BrowseProductsComponent} from "./views/products/browse-products/browse-products.component";
import {BrowseSuppliesComponent} from "./views/warehouse/browse-supplies/browse-supplies.component";

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'add-user', component: AddUserComponent },
  { path: 'edit-user/:id', component: AddUserComponent },
  { path: 'browse-users', component: BrowseUsersComponent },
  { path: 'default-users', component: DefaultUsersComponent },
  { path: 'add-product', component: AddProductComponent },
  { path: 'browse-products', component: BrowseProductsComponent },
  { path: 'edit-product/:type/:id', component: AddProductComponent },
  { path: 'browse-supplies', component: BrowseSuppliesComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
