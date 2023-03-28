import {EUnit} from "../../enums/EUnit";

export interface ProductCode{
  id: number;
  name: string;
  code: string;
  unit: EUnit;
}
