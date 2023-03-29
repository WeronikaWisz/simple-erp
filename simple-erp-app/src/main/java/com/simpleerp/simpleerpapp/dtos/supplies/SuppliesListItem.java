package com.simpleerp.simpleerpapp.dtos.supplies;

import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.enums.EUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String warningMessage;

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
}
