import {EType} from "../../enums/EType";
import {EUnit} from "../../enums/EUnit";
import {ProductQuantity} from "./ProductQuantity";

export interface AddProductRequest {
  type: EType;
  code: string;
  name: string;
  unit: EUnit;
  purchasePrice?: string;
  salePrice?: string;
  productSet?: ProductQuantity[];
}
