package com.simpleerp.simpleerpapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"email"}),
        @UniqueConstraint(columnNames={"name"})})
@Setter
@NoArgsConstructor
public class Contractor {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
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
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public Contractor(String name, String country, String nip, String bankAccount, String accountNumber, String url,
                      String email, String phone, String postalCode, String post, String city, String street,
                      String buildingNumber, String doorNumber, LocalDateTime creationDate) {
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
        this.creationDate = creationDate;
    }
}
