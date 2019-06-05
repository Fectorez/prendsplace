import { Moment } from 'moment';
import { IInterest } from 'app/shared/model/interest.model';

export interface IEvent {
  id?: number;
  name?: string;
  date?: Moment;
  location?: string;
  interests?: IInterest[];
  userLogin?: string;
  userId?: number;
}

export class Event implements IEvent {
  constructor(
    public id?: number,
    public name?: string,
    public date?: Moment,
    public location?: string,
    public interests?: IInterest[],
    public userLogin?: string,
    public userId?: number
  ) {}
}
