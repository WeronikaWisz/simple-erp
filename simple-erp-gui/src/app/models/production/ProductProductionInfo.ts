import {EUnit} from "../../enums/EUnit";
import {ProductionStepDescription} from "../products/ProductionStepDescription";
import {ProductionProductQuantity} from "./ProductionProductQuantity";

export interface ProductProductionInfo{
  code: string;
  name: string;
  unit: EUnit;
  productSet: ProductionProductQuantity[];
  productionSteps: ProductionStepDescription[];
}
