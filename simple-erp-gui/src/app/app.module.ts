import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainNavComponent } from './main-nav/main-nav.component';
import {authInterceptorProviders} from "./helpers/auth.interceptor";
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import { LoginComponent } from './views/manage-users/login/login.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {MatMenuModule} from "@angular/material/menu";
import {MatCardModule} from "@angular/material/card";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import { MatButtonModule } from '@angular/material/button'
import {MatStepperModule} from "@angular/material/stepper";
import {ChangePasswordDialogComponent} from "./views/manage-users/profile/change-password-dialog/change-password-dialog.component";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatTabsModule} from "@angular/material/tabs";
import {MatRippleModule} from "@angular/material/core";
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatDialogModule} from "@angular/material/dialog";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatSelectModule} from "@angular/material/select";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS, MatMomentDateModule} from "@angular/material-moment-adapter"
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import {UpdateProfileDialogComponent} from "./views/manage-users/profile/update-profile-dialog/update-profile-dialog.component";
import {ProfileComponent} from "./views/manage-users/profile/profile.component";
import { AddUserComponent } from './views/manage-users/add-user/add-user.component';
import { BrowseUsersComponent } from './views/manage-users/browse-users/browse-users.component';
import {MatTableModule} from "@angular/material/table";
import {MatChipsModule} from "@angular/material/chips";
import { DefaultUsersComponent } from './views/manage-users/default-users/default-users.component';
import { ChangeDefaultUserDialogComponent } from './views/manage-users/default-users/change-default-user-dialog/change-default-user-dialog.component';
import { AddProductComponent } from './views/products/add-product/add-product.component';
import { BrowseProductsComponent } from './views/products/browse-products/browse-products.component';
import { BrowseSuppliesComponent } from './views/warehouse/browse-supplies/browse-supplies.component';
import { UpdateSuppliesDialogComponent } from './views/warehouse/browse-supplies/update-supplies-dialog/update-supplies-dialog.component';
import { DelegatePurchaseDialogComponent } from './views/warehouse/browse-supplies/delegate-purchase-dialog/delegate-purchase-dialog.component';
import {MatTooltipModule} from "@angular/material/tooltip";
import { BrowseDelegatedTasksComponent } from './views/warehouse/browse-delegated-tasks/browse-delegated-tasks.component';
import { AssignedUsersDialogComponent } from './views/warehouse/browse-delegated-tasks/assigned-users-dialog/assigned-users-dialog.component';
import { AddOrderComponent } from './views/trade/add-order/add-order.component';
import { BrowseOrdersComponent } from './views/trade/browse-orders/browse-orders.component';
import { CustomerDialogComponent } from './views/trade/browse-orders/customer-dialog/customer-dialog.component';
import { ChangeUserDialogComponent } from './views/trade/browse-orders/change-user-dialog/change-user-dialog.component';
import { OrderInfoDialogComponent } from './views/trade/browse-orders/order-info-dialog/order-info-dialog.component';
import { BrowseReleaseComponent } from './views/warehouse/browse-release/browse-release.component';
import { BrowseAcceptanceComponent } from './views/warehouse/browse-acceptance/browse-acceptance.component';
import { ChangeUserReleaseDialogComponent } from './views/warehouse/browse-release/change-user-release-dialog/change-user-release-dialog.component';
import { ReleaseInfoDialogComponent } from './views/warehouse/browse-release/release-info-dialog/release-info-dialog.component';
import { BrowseDelegatedWarehouseTaskComponent } from './views/trade/browse-delegated-warehouse-task/browse-delegated-warehouse-task.component';
import { BrowsePurchasesComponent } from './views/trade/browse-purchases/browse-purchases.component';
import { PurchaseOrderNumberDialogComponent } from './views/trade/browse-purchases/purchase-order-number-dialog/purchase-order-number-dialog.component';
import { AddContractorComponent } from './views/products/add-contractor/add-contractor.component';
import { BrowseContractorsComponent } from './views/products/browse-contractors/browse-contractors.component';
import { AcceptanceInfoDialogComponent } from './views/warehouse/browse-acceptance/acceptance-info-dialog/acceptance-info-dialog.component';
import { ContractorDialogComponent } from './views/products/browse-contractors/contractor-dialog/contractor-dialog.component';
import { BrowseProductionComponent } from './views/production/browse-production/browse-production.component';
import { ChangeProductionUserDialogComponent } from './views/production/browse-production/change-production-user-dialog/change-production-user-dialog.component';
import { ForecastingComponent } from './views/settings/forecasting/forecasting.component';
import { ProductionInfoDialogComponent } from './views/production/browse-production/production-info-dialog/production-info-dialog.component';
import { ProductSetInfoDialogComponent } from './views/products/browse-products/product-set-info-dialog/product-set-info-dialog.component';
import { BrowseProductionProductsComponent } from './views/production/browse-production-products/browse-production-products.component';
import { ImportOrdersDialogComponent } from './views/trade/browse-orders/import-orders-dialog/import-orders-dialog.component';
import { BrowseAnalysisComponent } from './views/settings/browse-analysis/browse-analysis.component';
import {LocalizedDatePipe} from "./helpers/localized-date.pipe";
import '@angular/common/locales/global/pl';

@NgModule({
  declarations: [
    AppComponent,
    MainNavComponent,
    LoginComponent,
    ChangePasswordDialogComponent,
    UpdateProfileDialogComponent,
    ProfileComponent,
    AddUserComponent,
    BrowseUsersComponent,
    DefaultUsersComponent,
    ChangeDefaultUserDialogComponent,
    AddProductComponent,
    BrowseProductsComponent,
    BrowseSuppliesComponent,
    UpdateSuppliesDialogComponent,
    DelegatePurchaseDialogComponent,
    BrowseDelegatedTasksComponent,
    AssignedUsersDialogComponent,
    AddOrderComponent,
    BrowseOrdersComponent,
    CustomerDialogComponent,
    ChangeUserDialogComponent,
    OrderInfoDialogComponent,
    BrowseReleaseComponent,
    BrowseAcceptanceComponent,
    ChangeUserReleaseDialogComponent,
    ReleaseInfoDialogComponent,
    BrowseDelegatedWarehouseTaskComponent,
    BrowsePurchasesComponent,
    PurchaseOrderNumberDialogComponent,
    AddContractorComponent,
    BrowseContractorsComponent,
    AcceptanceInfoDialogComponent,
    ContractorDialogComponent,
    BrowseProductionComponent,
    ChangeProductionUserDialogComponent,
    ForecastingComponent,
    ProductionInfoDialogComponent,
    ProductSetInfoDialogComponent,
    BrowseProductionProductsComponent,
    ImportOrdersDialogComponent,
    BrowseAnalysisComponent,
    LocalizedDatePipe
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        }),
        BrowserAnimationsModule,
        MatSidenavModule,
        MatToolbarModule,
        MatListModule,
        MatIconModule,
        MatMenuModule,
        MatCardModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        MatInputModule,
        MatButtonModule,
        MatStepperModule,
        MatPaginatorModule,
        MatTabsModule,
        MatRippleModule,
        FormsModule,
        MatDialogModule,
        MatCheckboxModule,
        MatSelectModule,
        MatDatepickerModule,
        MatMomentDateModule,
        MatTableModule,
        MatChipsModule,
        MatTooltipModule
    ],
  providers: [
    authInterceptorProviders,
    {
      provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS,
      useValue: { useUtc: true }
    }
    ],
  bootstrap: [AppComponent]
})
export class AppModule { }

export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http);
}
