package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.supplies.SuppliesListItem;
import com.simpleerp.simpleerpapp.dtos.supplies.SuppliesResponse;
import com.simpleerp.simpleerpapp.models.*;
import com.simpleerp.simpleerpapp.repositories.StockLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuppliesService {

    private final StockLevelRepository stockLevelRepository;

    @Autowired
    public SuppliesService(StockLevelRepository stockLevelRepository) {
        this.stockLevelRepository = stockLevelRepository;
    }


    public SuppliesResponse loadSupplies(int page, int size) {
        SuppliesResponse suppliesResponse = new SuppliesResponse();
        List<StockLevel> stockLevelList = stockLevelRepository.findAll();
        int total = stockLevelList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            suppliesResponse.setSuppliesList(suppliesListToSuppliesListItem(stockLevelList.stream()
                    .sorted(Comparator.comparing(StockLevel::getCreationDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        suppliesResponse.setTotalProductsLength(total);
        return suppliesResponse;
    }

    private List<SuppliesListItem> suppliesListToSuppliesListItem(List<StockLevel> stockLevelList){
        List<SuppliesListItem> suppliesListItems = new ArrayList<>();
        for(StockLevel stockLevel: stockLevelList){
            SuppliesListItem suppliesListItem = new SuppliesListItem(stockLevel.getId(), stockLevel.getProduct().getType(),
                    stockLevel.getProduct().getCode(), stockLevel.getProduct().getName(), stockLevel.getProduct().getUnit(),
                    stockLevel.getQuantity().toString());
            suppliesListItem.setQuantityLessThanMin(stockLevel.getQuantity().compareTo(stockLevel.getMinQuantity()) < 0);
            // TODO here will be message that supplies end in a few days
            suppliesListItem.setWarningMessage("");
            suppliesListItems.add(suppliesListItem);
        }
        return suppliesListItems;
    }
}
