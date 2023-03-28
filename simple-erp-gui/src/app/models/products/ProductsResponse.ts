import {ProductListItem} from "./ProductListItem";

export interface ProductsResponse {
  productsList: ProductListItem[];
  totalProductsLength: number;
}
