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
import {BrowseDelegatedTasksComponent} from "./views/warehouse/browse-delegated-tasks/browse-delegated-tasks.component";
import {AddOrderComponent} from "./views/trade/add-order/add-order.component";
import {BrowseOrdersComponent} from "./views/trade/browse-orders/browse-orders.component";
import {BrowseAcceptanceComponent} from "./views/warehouse/browse-acceptance/browse-acceptance.component";
import {BrowseReleaseComponent} from "./views/warehouse/browse-release/browse-release.component";
import {BrowsePurchasesComponent} from "./views/trade/browse-purchases/browse-purchases.component";
import {BrowseDelegatedWarehouseTaskComponent} from "./views/trade/browse-delegated-warehouse-task/browse-delegated-warehouse-task.component";

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
  { path: 'browse-delegated-tasks', component: BrowseDelegatedTasksComponent },
  { path: 'add-order', component: AddOrderComponent },
  { path: 'browse-orders', component: BrowseOrdersComponent },
  { path: 'edit-order/:id', component: AddOrderComponent },
  { path: 'browse-acceptance', component: BrowseAcceptanceComponent },
  { path: 'browse-release', component: BrowseReleaseComponent },
  { path: 'browse-purchases', component: BrowsePurchasesComponent },
  { path: 'browse-delegated-warehouse-task', component: BrowseDelegatedWarehouseTaskComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
