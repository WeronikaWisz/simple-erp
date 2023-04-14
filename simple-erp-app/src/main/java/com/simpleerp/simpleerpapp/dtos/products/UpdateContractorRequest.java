package com.simpleerp.simpleerpapp.dtos.products;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateContractorRequest {
    private Long id;
    private String name;
    private String country;
    private String nip;
    private String bankAccount;
    private String accountNumber;
    private String url;
    private String email;
    private String phone;
    private String postalCode;
    private String post;
    private String city;
    private String street;
    private String buildingNumber;
    private String doorNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateContractorRequest that = (UpdateContractorRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(country, that.country)
                && Objects.equals(nip, that.nip) && Objects.equals(bankAccount, that.bankAccount)
                && Objects.equals(accountNumber, that.accountNumber) && Objects.equals(url, that.url)
                && Objects.equals(email, that.email) && Objects.equals(phone, that.phone)
                && Objects.equals(postalCode, that.postalCode) && Objects.equals(post, that.post)
                && Objects.equals(city, that.city) && Objects.equals(street, that.street)
                && Objects.equals(buildingNumber, that.buildingNumber) && Objects.equals(doorNumber, that.doorNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, country, nip, bankAccount, accountNumber, url, email, phone, postalCode, post, city, street, buildingNumber, doorNumber);
    }
}
