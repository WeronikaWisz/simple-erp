package com.simpleerp.simpleerpapp.dtos.products;

import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.enums.EUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class AddProductRequest {
    private EType type;
    private String code;
    private String name;
    private EUnit unit;
    private String purchasePrice;
    private String purchaseVat;
    private String salePrice;
    private String saleVat;
    private Long contractor;
    private List<ProductQuantity> productSet = new ArrayList<>();
    private List<ProductStepDescription> productionSteps = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddProductRequest that = (AddProductRequest) o;
        return type == that.type && Objects.equals(code, that.code) && Objects.equals(name, that.name)
                && unit == that.unit && Objects.equals(purchasePrice, that.purchasePrice)
                && Objects.equals(purchaseVat, that.purchaseVat) && Objects.equals(salePrice, that.salePrice)
                && Objects.equals(saleVat, that.saleVat) && Objects.equals(contractor, that.contractor)
                && Objects.equals(productSet, that.productSet) && Objects.equals(productionSteps, that.productionSteps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, code, name, unit, purchasePrice, purchaseVat, salePrice, saleVat,
                contractor, productSet, productionSteps);
    }
}
