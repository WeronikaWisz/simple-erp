package com.simpleerp.simpleerpapp.dtos.trade;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class OrderProductQuantity implements Serializable {
    private String product;
    private String quantity;
}
