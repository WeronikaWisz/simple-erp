import {EDirection} from "../../enums/EDirection";
import {ReleaseProductQuantity} from "./ReleaseProductQuantity";

export interface ReleaseDetails{
  id: number;
  number: string;
  association: string;
  orderDate: string;
  executionDate?: string;
  direction: EDirection;
  name: string;
  surname: string;
  email: string;
  phone?: string;
  postalCode?: string;
  post?: string;
  city?: string;
  street?: string;
  buildingNumber?: string;
  doorNumber?: string;
  productSet: ReleaseProductQuantity[];
}
