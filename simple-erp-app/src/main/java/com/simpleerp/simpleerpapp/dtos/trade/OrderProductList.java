package com.simpleerp.simpleerpapp.dtos.trade;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderProductList implements Serializable {
    private List<OrderProductQuantity> orderProductQuantityList;
}
