import {EStatus} from "../../enums/EStatus";

export interface OrderListItem{
  id: number;
  number: string;
  orderDate: string;
  price: string;
  customerId: number;
  customerName: string;
  assignedUserName: string;
  assignedUserId: number;
  status: EStatus;
  isIssued: boolean;
}
