import {ChartData} from "./ChartData";

export interface SalesAndExpensesData{
  quantity: ChartData[];
  sale: ChartData[];
  purchase: ChartData[];
  quantityToday: string;
  quantityMonth: string;
  saleToday: string;
  saleMonth: string;
  purchaseToday: string;
  purchaseMonth: string;
}
