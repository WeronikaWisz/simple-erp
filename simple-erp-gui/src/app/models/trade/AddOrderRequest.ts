import {OrderProductQuantity} from "./OrderProductQuantity";

export interface AddOrderRequest{
  number: string;
  orderDate: Date;
  discount?: string;
  delivery?: string;
  price?: string;
  name: string;
  surname: string;
  email: string;
  phone?: string;
  postalCode: string;
  post: string;
  city: string;
  street: string;
  buildingNumber: string;
  doorNumber?: string;
  productSet: OrderProductQuantity[];
}
