import {ReleaseAcceptanceListItem} from "./ReleaseAcceptanceListItem";

export interface ReleasesAcceptancesResponse {
  releasesList: ReleaseAcceptanceListItem[];
  totalTasksLength: number;
}
