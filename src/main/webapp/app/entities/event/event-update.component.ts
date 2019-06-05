import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';
import { IEvent, Event } from 'app/shared/model/event.model';
import { EventService } from './event.service';
import { IInterest } from 'app/shared/model/interest.model';
import { InterestService } from 'app/entities/interest';
import { IUser, UserService } from 'app/core';

@Component({
  selector: 'jhi-event-update',
  templateUrl: './event-update.component.html'
})
export class EventUpdateComponent implements OnInit {
  event: IEvent;
  isSaving: boolean;

  interests: IInterest[];

  users: IUser[];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    date: [],
    location: [],
    interests: [],
    userId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected eventService: EventService,
    protected interestService: InterestService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ event }) => {
      this.updateForm(event);
      this.event = event;
    });
    this.interestService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IInterest[]>) => mayBeOk.ok),
        map((response: HttpResponse<IInterest[]>) => response.body)
      )
      .subscribe((res: IInterest[]) => (this.interests = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(event: IEvent) {
    this.editForm.patchValue({
      id: event.id,
      name: event.name,
      date: event.date != null ? event.date.format(DATE_TIME_FORMAT) : null,
      location: event.location,
      interests: event.interests,
      userId: event.userId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const event = this.createFromForm();
    if (event.id !== undefined) {
      this.subscribeToSaveResponse(this.eventService.update(event));
    } else {
      this.subscribeToSaveResponse(this.eventService.create(event));
    }
  }

  private createFromForm(): IEvent {
    const entity = {
      ...new Event(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      date: this.editForm.get(['date']).value != null ? moment(this.editForm.get(['date']).value, DATE_TIME_FORMAT) : undefined,
      location: this.editForm.get(['location']).value,
      interests: this.editForm.get(['interests']).value,
      userId: this.editForm.get(['userId']).value
    };
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvent>>) {
    result.subscribe((res: HttpResponse<IEvent>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

  trackInterestById(index: number, item: IInterest) {
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
