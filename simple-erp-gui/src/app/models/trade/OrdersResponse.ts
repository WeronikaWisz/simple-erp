import {OrderListItem} from "./OrderListItem";

export interface OrdersResponse{
  ordersList: OrderListItem[];
  totalOrdersLength: number;
}
