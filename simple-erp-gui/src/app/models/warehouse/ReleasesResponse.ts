import {ReleaseListItem} from "./ReleaseListItem";

export interface ReleasesResponse{
  releasesList: ReleaseListItem[];
  totalTasksLength: number;
}
