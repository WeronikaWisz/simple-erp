package com.simpleerp.simpleerpapp.dtos.trade;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class DelegateExternalAcceptance {
    private List<Long> ids;
    private String orderNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelegateExternalAcceptance that = (DelegateExternalAcceptance) o;
        return Objects.equals(ids, that.ids) && Objects.equals(orderNumber, that.orderNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ids, orderNumber);
    }
}
