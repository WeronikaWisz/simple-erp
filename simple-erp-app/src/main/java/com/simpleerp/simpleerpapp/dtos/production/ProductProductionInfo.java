package com.simpleerp.simpleerpapp.dtos.production;

import com.simpleerp.simpleerpapp.dtos.products.ProductQuantity;
import com.simpleerp.simpleerpapp.dtos.products.ProductStepDescription;
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
public class ProductProductionInfo {
    private String code;
    private String name;
    private EUnit unit;
    private List<ProductionProductQuantity> productSet = new ArrayList<>();
    private List<ProductStepDescription> productionSteps = new ArrayList<>();

    public ProductProductionInfo(String code, String name, EUnit unit) {
        this.code = code;
        this.name = name;
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductProductionInfo that = (ProductProductionInfo) o;
        return Objects.equals(code, that.code) && Objects.equals(name, that.name) && unit == that.unit
                && Objects.equals(productSet, that.productSet) && Objects.equals(productionSteps, that.productionSteps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, unit, productSet, productionSteps);
    }
}
