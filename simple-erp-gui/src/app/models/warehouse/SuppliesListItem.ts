import {EType} from "../../enums/EType";
import {EUnit} from "../../enums/EUnit";

export interface SuppliesListItem {
  id: number
  type: EType;
  code: string;
  name: string;
  unit: EUnit;
  quantity: string;
  isQuantityLessThanMin: boolean;
  warningMessage: string;
}
