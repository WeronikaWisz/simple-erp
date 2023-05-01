import {ProductionProductQuantity} from "../production/ProductionProductQuantity";

export interface ProductSetInfo {
  code: string;
  name: string;
  productSet: ProductionProductQuantity[];
}
