import {EType} from "../../enums/EType";
import {EUnit} from "../../enums/EUnit";
import {ProductQuantity} from "./ProductQuantity";

export interface ProductListItem {
  id: number
  type: EType;
  code: string;
  name: string;
  unit: EUnit;
  purchasePrice?: string;
  purchaseVat?: string;
  salePrice?: string;
  saleVat?: string;
  productSet?: ProductQuantity[];
  contractor?: number;
}
