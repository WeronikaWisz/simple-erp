import {ETask} from "../../enums/ETask";

export interface UpdateAssignedUserRequest {
  taskIds: number[];
  employeeId: number;
  task: ETask;
}
