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
    private String code;
    private String name;
    private EUnit unit;
    private String quantity;
    private EStatus status;
    private String assignedUserName;
    private Long assignedUserId;
    private String creationDate;
    private String stockQuantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelegatedTaskListItem that = (DelegatedTaskListItem) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code) && Objects.equals(name, that.name)
                && unit == that.unit && Objects.equals(quantity, that.quantity) && status == that.status
                && Objects.equals(assignedUserName, that.assignedUserName) && Objects.equals(assignedUserId, that.assignedUserId)
                && Objects.equals(creationDate, that.creationDate) && Objects.equals(stockQuantity, that.stockQuantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, unit, quantity, status, assignedUserName, assignedUserId, creationDate, stockQuantity);
    }
}
