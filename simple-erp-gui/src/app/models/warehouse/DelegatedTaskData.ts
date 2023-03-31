import {EUnit} from "../../enums/EUnit";

export interface DelegatedTaskData{
  taskId?: number;
  stockLevelId?: number;
  code: string;
  name: string;
  unit: EUnit;
  stockQuantity: string;
  quantity?: string;
}
