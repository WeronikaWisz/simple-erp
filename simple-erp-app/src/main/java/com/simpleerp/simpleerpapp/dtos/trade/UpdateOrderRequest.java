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
public class UpdateOrderRequest {
    private Long id;
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

    public UpdateOrderRequest(Long id, String number, LocalDateTime orderDate, String discount, String delivery,
                              String price, String name, String surname, String email, String phone, String postalCode,
                              String post, String city, String street, String buildingNumber, String doorNumber) {
        this.id = id;
        this.number = number;
        this.orderDate = orderDate;
        this.discount = discount;
        this.delivery = delivery;
        this.price = price;
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
        UpdateOrderRequest that = (UpdateOrderRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(number, that.number)
                && Objects.equals(orderDate, that.orderDate) && Objects.equals(discount, that.discount)
                && Objects.equals(delivery, that.delivery) && Objects.equals(price, that.price)
                && Objects.equals(name, that.name) && Objects.equals(surname, that.surname)
                && Objects.equals(email, that.email) && Objects.equals(phone, that.phone)
                && Objects.equals(postalCode, that.postalCode) && Objects.equals(post, that.post)
                && Objects.equals(city, that.city) && Objects.equals(street, that.street)
                && Objects.equals(buildingNumber, that.buildingNumber) && Objects.equals(doorNumber, that.doorNumber)
                && Objects.equals(productSet, that.productSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, orderDate, discount, delivery, price, name, surname, email, phone, postalCode, post, city, street, buildingNumber, doorNumber, productSet);
    }
}
