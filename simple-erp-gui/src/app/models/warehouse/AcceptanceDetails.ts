import {EDirection} from "../../enums/EDirection";
import {ReleaseProductQuantity} from "./ReleaseProductQuantity";

export interface AcceptanceDetails{
  id: number;
  number: string;
  association: string;
  orderNumber?: string;
  orderDate: string;
  executionDate?: string;
  direction: EDirection;
  name: string;
  surname: string;
  country?: string;
  nip?: string;
  email: string;
  phone?: string;
  url?: string;
  postalCode?: string;
  post?: string;
  city?: string;
  street?: string;
  buildingNumber?: string;
  doorNumber?: string;
  bankAccount?: string;
  accountNumber?: string;
  productSet: ReleaseProductQuantity[];
}
