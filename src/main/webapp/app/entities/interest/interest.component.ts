import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IInterest } from 'app/shared/model/interest.model';
import { AccountService } from 'app/core';
import { InterestService } from './interest.service';

@Component({
  selector: 'jhi-interest',
  templateUrl: './interest.component.html'
})
export class InterestComponent implements OnInit, OnDestroy {
  interests: IInterest[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected interestService: InterestService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.interestService
      .query()
      .pipe(
        filter((res: HttpResponse<IInterest[]>) => res.ok),
        map((res: HttpResponse<IInterest[]>) => res.body)
      )
      .subscribe(
        (res: IInterest[]) => {
          this.interests = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInInterests();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IInterest) {
    return item.id;
  }

  registerChangeInInterests() {
    this.eventSubscriber = this.eventManager.subscribe('interestListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
