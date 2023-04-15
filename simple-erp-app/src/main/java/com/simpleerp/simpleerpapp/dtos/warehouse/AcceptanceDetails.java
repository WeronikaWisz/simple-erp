package com.simpleerp.simpleerpapp.dtos.warehouse;

import com.simpleerp.simpleerpapp.enums.EDirection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AcceptanceDetails {
    private Long id;
    private String number;
    private String association;
    private String orderDate;
    private String orderNumber;
    private String executionDate;
    private EDirection direction;
    private String name;
    private String surname;
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
    private List<ReleaseProductQuantity> productSet = new ArrayList<>();

    public AcceptanceDetails(Long id, String number, String association, String orderDate, String orderNumber,
                             String executionDate, EDirection direction, String name, String country, String nip,
                             String bankAccount, String accountNumber, String url, String email, String phone,
                             String postalCode, String post, String city, String street, String buildingNumber,
                             String doorNumber) {
        this.id = id;
        this.number = number;
        this.association = association;
        this.orderDate = orderDate;
        this.orderNumber = orderNumber;
        this.executionDate = executionDate;
        this.direction = direction;
        this.name = name;
        this.country = country;
        this.nip = nip;
        this.bankAccount = bankAccount;
        this.accountNumber = accountNumber;
        this.url = url;
        this.email = email;
        this.phone = phone;
        this.postalCode = postalCode;
        this.post = post;
        this.city = city;
        this.street = street;
        this.buildingNumber = buildingNumber;
        this.doorNumber = doorNumber;
    }

    public AcceptanceDetails(Long id, String number, String association, String orderDate, String orderNumber,
                             String executionDate, EDirection direction, String name) {
        this.id = id;
        this.number = number;
        this.association = association;
        this.orderDate = orderDate;
        this.orderNumber = orderNumber;
        this.executionDate = executionDate;
        this.direction = direction;
        this.name = name;
    }

    public AcceptanceDetails(Long id, String number, String association, String orderDate, String executionDate,
                             EDirection direction, String name, String surname, String email, String phone) {
        this.id = id;
        this.number = number;
        this.association = association;
        this.orderDate = orderDate;
        this.executionDate = executionDate;
        this.direction = direction;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcceptanceDetails that = (AcceptanceDetails) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(surname, that.surname)
                && Objects.equals(country, that.country) && Objects.equals(nip, that.nip) && Objects.equals(bankAccount, that.bankAccount)
                && Objects.equals(accountNumber, that.accountNumber) && Objects.equals(url, that.url) && Objects.equals(email, that.email)
                && Objects.equals(phone, that.phone) && Objects.equals(postalCode, that.postalCode) && Objects.equals(post, that.post)
                && Objects.equals(city, that.city) && Objects.equals(street, that.street) && Objects.equals(buildingNumber, that.buildingNumber)
                && Objects.equals(doorNumber, that.doorNumber) && Objects.equals(number, that.number) && Objects.equals(association, that.association)
                && Objects.equals(orderDate, that.orderDate) && Objects.equals(orderNumber, that.orderNumber)
                && Objects.equals(executionDate, that.executionDate) && direction == that.direction
                && Objects.equals(productSet, that.productSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, country, nip, bankAccount, accountNumber, url, email, phone, postalCode,
                post, city, street, buildingNumber, doorNumber, number, association, orderDate, orderNumber, executionDate,
                direction, productSet);
    }
}
