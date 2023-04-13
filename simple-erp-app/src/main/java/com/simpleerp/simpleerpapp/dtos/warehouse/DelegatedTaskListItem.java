package com.simpleerp.simpleerpapp.dtos.warehouse;

import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.enums.EUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DelegatedTaskListItem {
    private Long id;
    private String number;
    private String orderNumber;
    private String code;
    private String name;
    private EUnit unit;
    private String quantity;
    private EStatus status;
    private String assignedUserName;
    private Long assignedUserId;
    private String creationDate;
    private String stockQuantity;
    private Boolean accepted;

    public DelegatedTaskListItem(Long id, String number, String code, String name, EUnit unit, String quantity,
                                 EStatus status, String assignedUserName, Long assignedUserId, String creationDate,
                                 String stockQuantity) {
        this.id = id;
        this.number = number;
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.status = status;
        this.assignedUserName = assignedUserName;
        this.assignedUserId = assignedUserId;
        this.creationDate = creationDate;
        this.stockQuantity = stockQuantity;
    }

    public DelegatedTaskListItem(Long id, String number, String code, String name, EUnit unit, String quantity,
                                 EStatus status, String assignedUserName, Long assignedUserId,
                                 String creationDate, String orderNumber, Boolean accepted) {
        this.id = id;
        this.number = number;
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.status = status;
        this.assignedUserName = assignedUserName;
        this.assignedUserId = assignedUserId;
        this.creationDate = creationDate;
        this.orderNumber = orderNumber;
        this.accepted = accepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelegatedTaskListItem that = (DelegatedTaskListItem) o;
        return Objects.equals(id, that.id) && Objects.equals(number, that.number) && Objects.equals(code, that.code)
                && Objects.equals(name, that.name) && unit == that.unit && Objects.equals(quantity, that.quantity)
                && status == that.status && Objects.equals(assignedUserName, that.assignedUserName)
                && Objects.equals(assignedUserId, that.assignedUserId) && Objects.equals(creationDate, that.creationDate)
                && Objects.equals(stockQuantity, that.stockQuantity) && Objects.equals(accepted, that.accepted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, code, name, unit, quantity, status, assignedUserName, assignedUserId, creationDate, stockQuantity, accepted);
    }
}
