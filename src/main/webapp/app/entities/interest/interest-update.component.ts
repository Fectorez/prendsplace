import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { IInterest, Interest } from 'app/shared/model/interest.model';
import { InterestService } from './interest.service';
import { IEvent } from 'app/shared/model/event.model';
import { EventService } from 'app/entities/event';
import { IUser, UserService } from 'app/core';

@Component({
  selector: 'jhi-interest-update',
  templateUrl: './interest-update.component.html'
})
export class InterestUpdateComponent implements OnInit {
  interest: IInterest;
  isSaving: boolean;

  events: IEvent[];

  users: IUser[];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    users: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected interestService: InterestService,
    protected eventService: EventService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ interest }) => {
      this.updateForm(interest);
      this.interest = interest;
    });
    this.eventService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IEvent[]>) => mayBeOk.ok),
        map((response: HttpResponse<IEvent[]>) => response.body)
      )
      .subscribe((res: IEvent[]) => (this.events = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(interest: IInterest) {
    this.editForm.patchValue({
      id: interest.id,
      name: interest.name,
      users: interest.users
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const interest = this.createFromForm();
    if (interest.id !== undefined) {
      this.subscribeToSaveResponse(this.interestService.update(interest));
    } else {
      this.subscribeToSaveResponse(this.interestService.create(interest));
    }
  }

  private createFromForm(): IInterest {
    const entity = {
      ...new Interest(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      users: this.editForm.get(['users']).value
    };
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInterest>>) {
    result.subscribe((res: HttpResponse<IInterest>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackEventById(index: number, item: IEvent) {
    return item.id;
  }

  trackUserById(index: number, item: IUser) {
    return item.id;
  }

  getSelected(selectedVals: Array<any>, option: any) {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
