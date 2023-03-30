package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.warehouse.PurchaseTaskRequest;
import com.simpleerp.simpleerpapp.dtos.warehouse.SuppliesListItem;
import com.simpleerp.simpleerpapp.dtos.warehouse.SuppliesResponse;
import com.simpleerp.simpleerpapp.dtos.warehouse.UpdateSuppliesRequest;
import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.enums.ETask;
import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.*;
import com.simpleerp.simpleerpapp.repositories.*;
import com.simpleerp.simpleerpapp.security.userdetails.UserDetailsI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final StockLevelRepository stockLevelRepository;
    private final PurchaseTaskRepository purchaseTaskRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private MessageSource messageSource;

    @Autowired
    public WarehouseService(StockLevelRepository stockLevelRepository, PurchaseTaskRepository purchaseTaskRepository,
                            UserRepository userRepository, TaskRepository taskRepository, MessageSource messageSource) {
        this.stockLevelRepository = stockLevelRepository;
        this.purchaseTaskRepository = purchaseTaskRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;
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
                    stockLevel.getQuantity().toString(), stockLevel.getMinQuantity().toString(), stockLevel.getDaysUntilStockLasts());
            if(stockLevel.getProduct().getType().equals(EType.BOUGHT) && purchaseTaskDelegated(stockLevel.getProduct())){
                suppliesListItem.setMessage(messageSource.getMessage(
                        "message.purchaseDelegated", null, LocaleContextHolder.getLocale()));
            //TODO here will be check id production task delegated
            } else {
//                 TODO here will be message that supplies end in a few days
                suppliesListItem.setMessage("");
//                suppliesListItem.setWarningMessage(true);
            }
            suppliesListItems.add(suppliesListItem);
        }
        return suppliesListItems;
    }

    private boolean purchaseTaskDelegated(Product product){
        List<PurchaseTask> purchaseTaskList = purchaseTaskRepository
                .findByProductAndStatusNotIn(product, List.of(EStatus.DONE,EStatus.CANCELED))
                .orElse(Collections.emptyList());
        return !purchaseTaskList.isEmpty();
    }

    @Transactional
    public void updateSupplies(UpdateSuppliesRequest updateSuppliesRequest) {
        StockLevel stockLevel = stockLevelRepository.findById(updateSuppliesRequest.getId())
                .orElseThrow(() -> new ApiNotFoundException("exception.stockLevelNotFound"));
        stockLevel.setQuantity(new BigDecimal(updateSuppliesRequest.getQuantity()));
        stockLevel.setMinQuantity(new BigDecimal(updateSuppliesRequest.getMinQuantity()));
        stockLevel.setDaysUntilStockLasts(updateSuppliesRequest.getDays());
        stockLevel.setUpdateDate(LocalDateTime.now());
        stockLevelRepository.save(stockLevel);
    }

    public void delegatePurchaseTask(PurchaseTaskRequest purchaseTaskRequest) {
        StockLevel stockLevel = stockLevelRepository.findById(purchaseTaskRequest.getId())
                .orElseThrow(() -> new ApiNotFoundException("exception.stockLevelNotFound"));
        PurchaseTask purchaseTask = new PurchaseTask();
        purchaseTask.setProduct(stockLevel.getProduct());
        purchaseTask.setQuantity(new BigDecimal(purchaseTaskRequest.getQuantity()));
        purchaseTask.setStatus(EStatus.WAITING);

        Task task = taskRepository.findByName(ETask.TASK_PURCHASE)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));
        purchaseTask.setAssignedUser(task.getDefaultUser());

        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
        purchaseTask.setRequestingUser(user);

        purchaseTask.setCreationDate(LocalDateTime.now());
        purchaseTaskRepository.save(purchaseTask);
    }

    private String getCurrentUserUsername(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }
}
