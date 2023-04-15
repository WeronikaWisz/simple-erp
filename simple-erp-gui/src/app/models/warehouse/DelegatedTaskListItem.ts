import {EUnit} from "../../enums/EUnit";
import {EStatus} from "../../enums/EStatus";

export interface DelegatedTaskListItem {
  id: number;
  number: string;
  orderNumber: string;
  code: string;
  name: string;
  unit: EUnit;
  quantity: string;
  status: EStatus;
  purchaserId: number;
  purchaserName: string;
  assignedUserName: string;
  assignedUserId: number;
  creationDate: string;
  stockQuantity: number;
  accepted: boolean;
}
