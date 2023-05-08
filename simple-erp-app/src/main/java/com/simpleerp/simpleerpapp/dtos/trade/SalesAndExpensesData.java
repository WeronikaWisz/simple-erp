package com.simpleerp.simpleerpapp.dtos.trade;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SalesAndExpensesData {
    private List<ChartData> quantity = new ArrayList<>();
    private List<ChartData> sale = new ArrayList<>();
    private List<ChartData> purchase = new ArrayList<>();
    private String quantityToday;
    private String quantityMonth;
    private String saleToday;
    private String saleMonth;
    private String purchaseToday;
    private String purchaseMonth;
}
