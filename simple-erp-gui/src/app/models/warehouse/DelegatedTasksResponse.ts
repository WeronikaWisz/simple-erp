import {DelegatedTaskListItem} from "./DelegatedTaskListItem";

export interface DelegatedTasksResponse{
  tasksList: DelegatedTaskListItem[];
  totalTasksLength: number;
}
