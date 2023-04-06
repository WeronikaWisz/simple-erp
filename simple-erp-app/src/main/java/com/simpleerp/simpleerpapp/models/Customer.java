package com.simpleerp.simpleerpapp.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"email"})})
@Setter
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    private String surname;
    @Nonnull
    private String email;
    private String phone;
    private String postalCode;
    private String post;
    private String city;
    private String street;
    private String buildingNumber;
    private String doorNumber;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public Customer(String name, String surname, String email, String phone, String postalCode, String post, String city,
                    String street, String buildingNumber, String doorNumber, LocalDateTime creationDate) {
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
        this.creationDate = creationDate;
    }
}
