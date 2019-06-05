import { IEvent } from 'app/shared/model/event.model';
import { IUser } from 'app/core/user/user.model';

export interface IInterest {
  id?: number;
  name?: string;
  events?: IEvent[];
  users?: IUser[];
}

export class Interest implements IInterest {
  constructor(public id?: number, public name?: string, public events?: IEvent[], public users?: IUser[]) {}
}
