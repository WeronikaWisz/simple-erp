package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.manageusers.UserName;
import com.simpleerp.simpleerpapp.dtos.products.ProductCode;
import com.simpleerp.simpleerpapp.dtos.trade.UpdateAssignedUserRequest;
import com.simpleerp.simpleerpapp.dtos.warehouse.*;
import com.simpleerp.simpleerpapp.enums.*;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final StockLevelRepository stockLevelRepository;
    private final PurchaseTaskRepository purchaseTaskRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ReleaseRepository releaseRepository;
    private final ProductRepository productRepository;
    private MessageSource messageSource;

    @Autowired
    public WarehouseService(StockLevelRepository stockLevelRepository, PurchaseTaskRepository purchaseTaskRepository,
                            UserRepository userRepository, TaskRepository taskRepository, MessageSource messageSource,
                            ReleaseRepository releaseRepository, ProductRepository productRepository) {
        this.stockLevelRepository = stockLevelRepository;
        this.purchaseTaskRepository = purchaseTaskRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.releaseRepository = releaseRepository;
        this.messageSource = messageSource;
        this.productRepository = productRepository;
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

    public DelegatedTasksResponse loadDelegatedTasks(EType type, int page, int size) {
        DelegatedTasksResponse delegatedTasksResponse = new DelegatedTasksResponse();
        if(type.equals(EType.BOUGHT)){
            loadDelegatedPurchaseTasks(delegatedTasksResponse, page, size);
        } else if (type.equals(EType.PRODUCED)){
            loadDelegatedProductionTasks(delegatedTasksResponse, page, size);
        } else {
            delegatedTasksResponse.setTasksList(Collections.emptyList());
            delegatedTasksResponse.setTotalTasksLength(0);
        }
        return delegatedTasksResponse;
    }

    private void loadDelegatedPurchaseTasks(DelegatedTasksResponse delegatedTasksResponse, int page, int size) {
        List<PurchaseTask> purchaseTaskList = purchaseTaskRepository
                .findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))
                .orElse(Collections.emptyList());
        int total = purchaseTaskList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            delegatedTasksResponse.setTasksList(purchaseTaskListToDelegatedTasksListItem(purchaseTaskList
                    .stream().sorted(Comparator.comparing(PurchaseTask::getCreationDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        delegatedTasksResponse.setTotalTasksLength(total);
    }

    private List<DelegatedTaskListItem> purchaseTaskListToDelegatedTasksListItem(List<PurchaseTask> purchaseTaskList){
        List<DelegatedTaskListItem> delegatedTaskListItems = new ArrayList<>();
        for(PurchaseTask purchaseTask: purchaseTaskList){
            DelegatedTaskListItem delegatedTaskListItem = new DelegatedTaskListItem(purchaseTask.getId(),
                    purchaseTask.getProduct().getCode(), purchaseTask.getProduct().getName(),
                    purchaseTask.getProduct().getUnit(), purchaseTask.getQuantity().toString(), purchaseTask.getStatus(),
                    purchaseTask.getAssignedUser().getName() + " " +  purchaseTask.getAssignedUser().getSurname(),
                    purchaseTask.getAssignedUser().getId(),
                    purchaseTask.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    findProductStockQuantity(purchaseTask.getProduct()).toString());
            delegatedTaskListItems.add(delegatedTaskListItem);
        }
        return delegatedTaskListItems;
    }

    private BigDecimal findProductStockQuantity(Product product){
        StockLevel stockLevel = stockLevelRepository.findByProduct(product)
                .orElseThrow(() -> new ApiNotFoundException("exception.stockLevelNotFound"));
        return stockLevel.getQuantity();
    }

    // TODO
    private void loadDelegatedProductionTasks(DelegatedTasksResponse delegatedTasksResponse, int page, int size) {
        delegatedTasksResponse.setTasksList(Collections.emptyList());
        delegatedTasksResponse.setTotalTasksLength(0);
    }

    @Transactional
    public void updatePurchaseTask(PurchaseTaskRequest purchaseTaskRequest) {
        PurchaseTask purchaseTask = purchaseTaskRepository.findById(purchaseTaskRequest.getId())
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));
        if(!purchaseTask.getStatus().equals(EStatus.WAITING)){
            throw new ApiExpectationFailedException("exception.taskNotWaiting");
        }
        purchaseTask.setQuantity(new BigDecimal(purchaseTaskRequest.getQuantity()));
        purchaseTask.setUpdateDate(LocalDateTime.now());
        purchaseTaskRepository.save(purchaseTask);
    }

    public void deleteTask(EType type, Long id) {
        if(type.equals(EType.BOUGHT)){
            deleteDelegatedPurchaseTasks(id);
        } else if (type.equals(EType.PRODUCED)){
            deleteDelegatedProductionTasks(id);
        }
    }

    private void deleteDelegatedPurchaseTasks(Long id) {
        PurchaseTask purchaseTask = purchaseTaskRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));
        if(!purchaseTask.getStatus().equals(EStatus.WAITING)){
            throw new ApiExpectationFailedException("exception.taskNotWaiting");
        }
        purchaseTaskRepository.delete(purchaseTask);
    }

    // TODO
    private void deleteDelegatedProductionTasks(Long id) {
    }

    public ReleasesResponse loadReleases(EStatus status, int page, int size) {
        ReleasesResponse releasesResponse = new ReleasesResponse();
        List<Release> releaseList = this.releaseRepository.findByStatus(status).orElse(Collections.emptyList());
        int total = releaseList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            releasesResponse.setReleasesList(releaseListToReleaseListItem(releaseList.stream()
                    .sorted(Comparator.comparing(Release::getCreationDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        releasesResponse.setTotalTasksLength(total);
        return releasesResponse;
    }

    public ReleasesResponse loadReleases(EStatus status, EDirection direction, int page, int size) {
        ReleasesResponse releasesResponse = new ReleasesResponse();
        List<Release> releaseList = this.releaseRepository.findByStatusAndDirection(status, direction)
                .orElse(Collections.emptyList());
        int total = releaseList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            releasesResponse.setReleasesList(releaseListToReleaseListItem(releaseList.stream()
                    .sorted(Comparator.comparing(Release::getCreationDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        releasesResponse.setTotalTasksLength(total);
        return releasesResponse;
    }

    private List<ReleaseListItem> releaseListToReleaseListItem(List<Release> releases){
        List<ReleaseListItem> releaseListItemList = new ArrayList<>();
        for(Release release: releases){
            ReleaseListItem releaseListItem = new ReleaseListItem(release.getId(), release.getNumber(),
                    release.getOrder().getNumber(),
                    release.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    release.getDirection(), release.getRequestingUser().getId(),
                    release.getRequestingUser().getName() + " " + release.getRequestingUser().getSurname(),
                    release.getAssignedUser().getName() + " " + release.getAssignedUser().getSurname(),
                    release.getAssignedUser().getId(), release.getStatus());
            releaseListItemList.add(releaseListItem);
        }
        return releaseListItemList;
    }

    @Transactional
    public void updateReleaseAssignedUsers(UpdateAssignedUserRequest updateAssignedUserRequest) {
        User user = userRepository.findById(updateAssignedUserRequest.getEmployeeId())
                .orElseThrow(() -> new ApiNotFoundException("exception.userNotFound"));
        for(Long id: updateAssignedUserRequest.getTaskIds()){
            Release release = releaseRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.releaseNotFound"));
            release.setAssignedUser(user);
            release.setUpdateDate(LocalDateTime.now());
            releaseRepository.save(release);
        }
    }

    public List<UserName> loadUsers() {
        List<User> userList = userRepository.findAll()
                .stream().filter(user -> user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                        .contains(ERole.ROLE_WAREHOUSE)).collect(Collectors.toList());
        Optional<User> admin = userRepository.findByUsername("admin");
        admin.ifPresent(userList::add);
        List<UserName> userNameList = new ArrayList<>();
        for (User user: userList){
            userNameList.add(new UserName(user.getId(), user.getName() + " " + user.getSurname()));
        }
        return userNameList;
    }

    @Transactional
    public void markReleaseAsInProgress(List<Long> ids) {
        for(Long id: ids){
            Release release = releaseRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.releaseNotFound"));
            release.setStatus(EStatus.IN_PROGRESS);
            release.setUpdateDate(LocalDateTime.now());
            releaseRepository.save(release);
        }
    }
    @Transactional
    public void markReleaseAsDone(List<Long> ids) {
        for(Long id: ids){
            Release release = releaseRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.releaseNotFound"));
            decreaseStockLevel(release);
            release.setStatus(EStatus.DONE);
            LocalDateTime currentDate = LocalDateTime.now();
            release.setUpdateDate(currentDate);
            release.setExecutionDate(currentDate);
            releaseRepository.save(release);
        }
    }

    private void decreaseStockLevel(Release release){
        for(OrderProducts orderProducts: release.getOrder().getOrderProductsSet()){
            StockLevel stockLevel = stockLevelRepository.findByProduct(orderProducts.getProduct())
                .orElseThrow(() -> new ApiNotFoundException("exception.stockLevelNotFound"));
            BigDecimal newQuantity = stockLevel.getQuantity().subtract(orderProducts.getQuantity());
            if(newQuantity.compareTo(BigDecimal.ZERO) < 0){
                throw new ApiExpectationFailedException("exception.stockLevelLessThanZero");
            }
            stockLevel.setQuantity(newQuantity);
            stockLevelRepository.save(stockLevel);
        }
    }

    public List<ProductCode> loadProductList() {
        List<Product> productList = productRepository.findAll();
        List<ProductCode> productCodeList = new ArrayList<>();
        for (Product product: productList){
            if(product.getSalePrice() == null){
                continue;
            }
            ProductCode productCode = new ProductCode();
            productCode.setId(product.getId());
            productCode.setName(product.getName());
            productCode.setCode(product.getCode());
            productCode.setUnit(product.getUnit());
            productCodeList.add(productCode);
        }
        return productCodeList;
    }

    public ReleaseDetails getRelease(Long id) {
        Release release = releaseRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.releaseNotFound"));
        ReleaseDetails releaseDetails = new ReleaseDetails(release.getId(), release.getNumber(),
                release.getOrder().getNumber(),
                release.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                release.getExecutionDate() != null ?
                        release.getExecutionDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "",
                release.getDirection(), release.getOrder().getCustomer().getName(),
                release.getOrder().getCustomer().getSurname(), release.getOrder().getCustomer().getEmail(),
                release.getOrder().getCustomer().getPhone(), release.getOrder().getCustomer().getPostalCode(),
                release.getOrder().getCustomer().getPost(), release.getOrder().getCustomer().getCity(),
                release.getOrder().getCustomer().getStreet(), release.getOrder().getCustomer().getBuildingNumber(),
                release.getOrder().getCustomer().getDoorNumber());
        List<ReleaseProductQuantity> releaseProductQuantityList = new ArrayList<>();
        for(OrderProducts orderProduct: release.getOrder().getOrderProductsSet()){
            boolean isProductInStock = this.isProductInStock(orderProduct);
            ReleaseProductQuantity releaseProductQuantity = new ReleaseProductQuantity(
                    orderProduct.getProduct().getCode(), orderProduct.getQuantity().toString(), isProductInStock);
            releaseProductQuantityList.add(releaseProductQuantity);
        }
        releaseDetails.setProductSet(releaseProductQuantityList);
        return releaseDetails;
    }

    private boolean isProductInStock(OrderProducts orderProduct) {
        StockLevel stockLevel = stockLevelRepository.findByProduct(orderProduct.getProduct())
                .orElseThrow(() -> new ApiNotFoundException("exception.stockLevelNotFound"));
        BigDecimal quantity = stockLevel.getQuantity().subtract(orderProduct.getQuantity());
        return (quantity.compareTo(BigDecimal.ZERO) > 0);
    }
}
