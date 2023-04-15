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
public class ReleaseDetails {
    private Long id;
    private String number;
    private String association;
    private String orderDate;
    private String executionDate;
    private EDirection direction;
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
    private List<ReleaseProductQuantity> productSet = new ArrayList<>();

    public ReleaseDetails(Long id, String number, String association, String orderDate, String executionDate,
                          EDirection direction, String name, String surname, String email, String phone,
                          String postalCode, String post, String city, String street, String buildingNumber,
                          String doorNumber) {
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
        this.postalCode = postalCode;
        this.post = post;
        this.city = city;
        this.street = street;
        this.buildingNumber = buildingNumber;
        this.doorNumber = doorNumber;
    }

    public ReleaseDetails(Long id, String number, String association, String orderDate, String executionDate,
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
        ReleaseDetails that = (ReleaseDetails) o;
        return Objects.equals(id, that.id) && Objects.equals(number, that.number)
                && Objects.equals(association, that.association) && Objects.equals(orderDate, that.orderDate)
                && Objects.equals(executionDate, that.executionDate) && direction == that.direction
                && Objects.equals(name, that.name) && Objects.equals(surname, that.surname)
                && Objects.equals(email, that.email) && Objects.equals(phone, that.phone)
                && Objects.equals(postalCode, that.postalCode) && Objects.equals(post, that.post)
                && Objects.equals(city, that.city) && Objects.equals(street, that.street)
                && Objects.equals(buildingNumber, that.buildingNumber) && Objects.equals(doorNumber, that.doorNumber)
                && Objects.equals(productSet, that.productSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, association, orderDate, executionDate, direction, name, surname, email, phone, postalCode, post, city, street, buildingNumber, doorNumber, productSet);
    }
}
