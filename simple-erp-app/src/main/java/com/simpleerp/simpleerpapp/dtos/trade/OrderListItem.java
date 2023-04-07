package com.simpleerp.simpleerpapp.dtos.trade;

import com.simpleerp.simpleerpapp.enums.EStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class OrderListItem {
    private Long id;
    private String number;
    private String orderDate;
    private String price;
    private Long customerId;
    private String customerName;
    private String assignedUserName;
    private Long assignedUserId;
    private EStatus status;
    private boolean isIssued;

    public OrderListItem(Long id, String number, String orderDate, String price, Long customerId, String customerName,
                         String assignedUserName, Long assignedUserId, EStatus status, boolean isIssued) {
        this.id = id;
        this.number = number;
        this.orderDate = orderDate;
        this.price = price;
        this.customerId = customerId;
        this.customerName = customerName;
        this.assignedUserName = assignedUserName;
        this.assignedUserId = assignedUserId;
        this.status = status;
        this.isIssued = isIssued;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderListItem that = (OrderListItem) o;
        return isIssued == that.isIssued && Objects.equals(id, that.id) && Objects.equals(number, that.number)
                && Objects.equals(orderDate, that.orderDate) && Objects.equals(price, that.price)
                && Objects.equals(customerId, that.customerId) && Objects.equals(customerName, that.customerName)
                && Objects.equals(assignedUserName, that.assignedUserName)
                && Objects.equals(assignedUserId, that.assignedUserId) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, orderDate, price, customerId, customerName, assignedUserName, assignedUserId, status, isIssued);
    }
}
