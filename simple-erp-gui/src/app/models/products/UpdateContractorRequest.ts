export interface UpdateContractorRequest{
  id: number;
  name: string;
  country: string;
  nip: string;
  email: string;
  phone?: string;
  url?: string;
  postalCode: string;
  post: string;
  city: string;
  street: string;
  buildingNumber: string;
  doorNumber?: string;
  bankAccount?: string;
  accountNumber?: string;
}
