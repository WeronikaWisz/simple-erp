import { Component, OnInit } from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {FormBuilder} from "@angular/forms";
import {ProductsService} from "../../../services/products.service";
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {ERole} from "../../../enums/ERole";
import Swal from "sweetalert2";
import {PageEvent} from "@angular/material/paginator";
import {ContractorListItem} from "../../../models/products/ContractorListItem";

@Component({
  selector: 'app-browse-contractors',
  templateUrl: './browse-contractors.component.html',
  styleUrls: ['./browse-contractors.component.sass']
})
export class BrowseContractorsComponent implements OnInit {



  isLoggedIn = false;
  isAdmin = false;

  displayedColumns = ['name', 'country', 'nip', 'email', 'phone', 'url', 'actions'];
  dataSource: MatTableDataSource<ContractorListItem> = new MatTableDataSource<ContractorListItem>([]);

  contractors: ContractorListItem[] = []
  contractorsCount: number = 0;

  totalContractorsLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  emptySearchList = false;

  constructor(private formBuilder: FormBuilder, private productsService: ProductsService,
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
    this.loadContractors();
  }

  loadContractors(){
    this.emptySearchList = false;
    this.productsService.loadContractors(this.pageIndex, this.pageSize)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.contractors = data.contractorsList;
          this.contractorsCount = data.totalContractorsLength
          this.totalContractorsLength = data.totalContractorsLength
          if (this.contractors.length == 0) {
            this.emptySearchList = true;
          }
          this.dataSource.data = this.contractors;
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("products.browse-contractors.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      })
  }

  deleteContractor(id: number){
    this.productsService.deleteContractor(id)
      .subscribe({
        next: (data) => {
          console.log(data);
          this.loadContractors();
        },
        error: (err) => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("products.browse-contractors.delete-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      });
  }

  goToEditContractor(id: number): void{
    this.router.navigate(['/edit-contractor', id]);
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
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadContractors();
  }

  goToAddContractor(){
    this.router.navigate(['/add-contractor']);
  }

}
