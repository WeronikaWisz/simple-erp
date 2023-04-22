import {EUnit} from "../../enums/EUnit";
import {EType} from "../../enums/EType";

export interface DelegatedTaskData{
  taskId?: number;
  taskType: EType;
  stockLevelId?: number;
  code: string;
  name: string;
  unit: EUnit;
  stockQuantity: string;
  quantity?: string;
}
