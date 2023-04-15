package com.simpleerp.simpleerpapp.dtos.trade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderRequest {
    private String number;
    private LocalDateTime orderDate;
    private String discount;
    private String delivery;
    private String price;
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
    private List<OrderProductQuantity> productSet = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddOrderRequest that = (AddOrderRequest) o;
        return Objects.equals(number, that.number) && Objects.equals(orderDate, that.orderDate) && Objects.equals(discount, that.discount) && Objects.equals(delivery, that.delivery) && Objects.equals(price, that.price) && Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Objects.equals(email, that.email) && Objects.equals(phone, that.phone) && Objects.equals(postalCode, that.postalCode) && Objects.equals(post, that.post) && Objects.equals(city, that.city) && Objects.equals(street, that.street) && Objects.equals(buildingNumber, that.buildingNumber) && Objects.equals(doorNumber, that.doorNumber) && Objects.equals(productSet, that.productSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, orderDate, discount, delivery, price, name, surname, email, phone, postalCode, post, city, street, buildingNumber, doorNumber, productSet);
    }
}
