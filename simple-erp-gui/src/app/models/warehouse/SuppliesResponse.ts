import {SuppliesListItem} from "./SuppliesListItem";

export interface SuppliesResponse{
  suppliesList: SuppliesListItem[];
  totalProductsLength: number;
}
