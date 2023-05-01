import {EUnit} from "../../enums/EUnit";

export interface ProductionProductListItem{
  productId: number;
  code: string;
  name: string;
  unit: EUnit;
}
