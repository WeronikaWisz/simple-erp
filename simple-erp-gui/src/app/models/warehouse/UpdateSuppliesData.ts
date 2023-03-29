import {EUnit} from "../../enums/EUnit";

export interface UpdateSuppliesData {
  id: number
  code: string;
  name: string;
  unit: EUnit;
  quantity: string;
  minQuantity: string;
  days: number;
}
