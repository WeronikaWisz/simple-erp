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
public class ContractorListItem {
    private Long id;
    private String name;
    private String country;
    private String nip;
    private String url;
    private String email;
    private String phone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractorListItem that = (ContractorListItem) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(country, that.country)
                && Objects.equals(nip, that.nip) && Objects.equals(url, that.url) && Objects.equals(email, that.email)
                && Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, country, nip, url, email, phone);
    }
}
