import {EStatus} from "../../enums/EStatus";
import {EDirection} from "../../enums/EDirection";

export interface ReleaseListItem{
  id: number;
  number: string;
  association: string;
  orderDate: string;
  direction: EDirection;
  purchaserId: number;
  purchaserName: string;
  assignedUserName: string;
  assignedUserId: number;
  status: EStatus;
}
