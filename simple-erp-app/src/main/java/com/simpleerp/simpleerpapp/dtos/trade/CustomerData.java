package com.simpleerp.simpleerpapp.dtos.trade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CustomerData {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String postalCode;
    private String post;
    private String city;
    private String street;
    private String buildingNumber;
    private String doorNumber;

    public CustomerData(String name, String surname, String email, String phone, String postalCode, String post,
                        String city, String street, String buildingNumber, String doorNumber) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.postalCode = postalCode;
        this.post = post;
        this.city = city;
        this.street = street;
        this.buildingNumber = buildingNumber;
        this.doorNumber = doorNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerData customer = (CustomerData) o;
        return Objects.equals(id, customer.id) && Objects.equals(name, customer.name)
                && Objects.equals(surname, customer.surname) && Objects.equals(email, customer.email)
                && Objects.equals(phone, customer.phone) && Objects.equals(postalCode, customer.postalCode)
                && Objects.equals(post, customer.post) && Objects.equals(city, customer.city)
                && Objects.equals(street, customer.street) && Objects.equals(buildingNumber, customer.buildingNumber)
                && Objects.equals(doorNumber, customer.doorNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, email, phone, postalCode, post, city, street, buildingNumber, doorNumber);
    }
}
