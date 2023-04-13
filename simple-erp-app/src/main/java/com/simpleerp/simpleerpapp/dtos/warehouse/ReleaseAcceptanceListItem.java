package com.simpleerp.simpleerpapp.dtos.warehouse;

import com.simpleerp.simpleerpapp.enums.EDirection;
import com.simpleerp.simpleerpapp.enums.EStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseAcceptanceListItem {
    private Long id;
    private String number;
    private String association;
    private String orderDate;
    private EDirection direction;
    private Long purchaserId;
    private String purchaserName;
    private String assignedUserName;
    private Long assignedUserId;
    private EStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseAcceptanceListItem that = (ReleaseAcceptanceListItem) o;
        return Objects.equals(id, that.id) && Objects.equals(number, that.number)
                && Objects.equals(association, that.association) && Objects.equals(orderDate, that.orderDate)
                && direction == that.direction && Objects.equals(purchaserId, that.purchaserId)
                && Objects.equals(purchaserName, that.purchaserName)
                && Objects.equals(assignedUserName, that.assignedUserName)
                && Objects.equals(assignedUserId, that.assignedUserId) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, association, orderDate, direction, purchaserId, purchaserName, assignedUserName, assignedUserId, status);
    }
}
