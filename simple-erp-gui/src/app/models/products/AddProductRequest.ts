import {EType} from "../../enums/EType";
import {EUnit} from "../../enums/EUnit";
import {ProductQuantity} from "./ProductQuantity";
import {ProductionStepDescription} from "./ProductionStepDescription";

export interface AddProductRequest {
  type: EType;
  code: string;
  name: string;
  unit: EUnit;
  purchasePrice?: string;
  salePrice?: string;
  productSet?: ProductQuantity[];
  contractor?: number;
  productionSteps?: ProductionStepDescription[];
}
