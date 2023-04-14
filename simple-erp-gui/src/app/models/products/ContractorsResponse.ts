import {ContractorListItem} from "./ContractorListItem";

export interface ContractorsResponse{
  contractorsList: ContractorListItem[];
  totalContractorsLength: number;
}
