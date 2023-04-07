package com.simpleerp.simpleerpapp.dtos.trade;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class OrdersResponse {
    private List<OrderListItem> ordersList;
    private Integer totalOrdersLength;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdersResponse that = (OrdersResponse) o;
        return Objects.equals(ordersList, that.ordersList) && Objects.equals(totalOrdersLength, that.totalOrdersLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordersList, totalOrdersLength);
    }
}
