import { Component, OnInit } from '@angular/core';
import {TokenStorageService} from "../services/token-storage.service";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {map, shareReplay, Observable} from 'rxjs';
import {ERole} from "../enums/ERole";

@Component({
  selector: 'app-main-nav',
  templateUrl: './main-nav.component.html',
  styleUrls: ['./main-nav.component.sass']
})
export class MainNavComponent implements OnInit {

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  private roles: string[] = [];
  isLoggedIn = false;
  username?: string;

  constructor(private tokenStorageService: TokenStorageService, public translate: TranslateService,
              public dialog: MatDialog, private router: Router, private breakpointObserver: BreakpointObserver) { }

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

  reloadPage(): void {
    window.location.reload();
  }

  hasAdminRole(): boolean{
    return this.roles.includes(ERole[ERole.ROLE_ADMIN]);
  }

  hasWarehouseRole(): boolean{
    return this.roles.includes(ERole[ERole.ROLE_WAREHOUSE]);
  }

  hasProductionRole(): boolean{
    return this.roles.includes(ERole[ERole.ROLE_PRODUCTION]);
  }

  hasTradeRole(): boolean{
    return this.roles.includes(ERole[ERole.ROLE_TRADE]);
  }

}
