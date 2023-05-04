package com.simpleerp.simpleerpapp.dtos.warehouse;

import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.enums.EUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class SuppliesListItem {
    private Long id;
    private EType type;
    private String code;
    private String name;
    private EUnit unit;
    private String quantity;
    private String minQuantity;
    private Integer days;
    private String message;
    private Boolean isWarningMessage = false;

    public SuppliesListItem(Long id, EType type, String code, String name, EUnit unit,
                            String quantity, String minQuantity, Integer days) {
        this.id = id;
        this.type = type;
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.days = days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuppliesListItem that = (SuppliesListItem) o;
        return Objects.equals(id, that.id) && type == that.type && Objects.equals(code, that.code)
                && Objects.equals(name, that.name) && unit == that.unit && Objects.equals(quantity, that.quantity)
                && Objects.equals(minQuantity, that.minQuantity) && Objects.equals(days, that.days)
                && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, code, name, unit, quantity, minQuantity, days, message);
    }
}
