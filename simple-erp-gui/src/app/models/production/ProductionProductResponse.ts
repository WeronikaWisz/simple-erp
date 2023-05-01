import {ProductionProductListItem} from "./ProductionProductListItem";

export interface ProductionProductResponse{
  productsList: ProductionProductListItem[];
  totalProductsLength: number;
}
