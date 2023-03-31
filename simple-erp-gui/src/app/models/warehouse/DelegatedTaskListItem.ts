import {EUnit} from "../../enums/EUnit";
import {EStatus} from "../../enums/EStatus";

export interface DelegatedTaskListItem {
  id: number;
  code: string;
  name: string;
  unit: EUnit;
  quantity: string;
  status: EStatus;
  assignedUserName: string;
  assignedUserId: number;
  creationDate: string;
  stockQuantity: number;
}
