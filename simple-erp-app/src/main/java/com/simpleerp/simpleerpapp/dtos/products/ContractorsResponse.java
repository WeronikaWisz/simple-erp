package com.simpleerp.simpleerpapp.dtos.products;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ContractorsResponse {
    private List<ContractorListItem> contractorsList;
    private Integer totalContractorsLength;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractorsResponse that = (ContractorsResponse) o;
        return Objects.equals(contractorsList, that.contractorsList) && Objects.equals(totalContractorsLength,
                that.totalContractorsLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractorsList, totalContractorsLength);
    }
}
