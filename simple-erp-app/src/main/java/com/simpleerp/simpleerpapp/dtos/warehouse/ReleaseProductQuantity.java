package com.simpleerp.simpleerpapp.dtos.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseProductQuantity {
    private String product;
    private String quantity;
    private Boolean isInStock;
}
