package com.simpleerp.simpleerpapp.dtos.trade;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderProductQuantity {
    private String product;
    private String quantity;
}
