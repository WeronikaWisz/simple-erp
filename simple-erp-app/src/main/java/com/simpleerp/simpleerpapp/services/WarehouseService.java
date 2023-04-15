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

    private static final String PURCHASE_PREFIX = "ZZ";
    private static final String PURCHASE_SEPARATOR = "/";

    private final StockLevelRepository stockLevelRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ReleaseRepository releaseRepository;
    private final ProductRepository productRepository;
    private final AcceptanceRepository acceptanceRepository;
    private final MessageSource messageSource;

    @Autowired
    public WarehouseService(StockLevelRepository stockLevelRepository, PurchaseRepository purchaseRepository,
                            UserRepository userRepository, TaskRepository taskRepository, MessageSource messageSource,
                            ReleaseRepository releaseRepository, ProductRepository productRepository,
                            AcceptanceRepository acceptanceRepository) {
        this.stockLevelRepository = stockLevelRepository;
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.releaseRepository = releaseRepository;
        this.messageSource = messageSource;
        this.productRepository = productRepository;
        this.acceptanceRepository = acceptanceRepository;
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
        List<Purchase> purchaseList = purchaseRepository
                .findByProductAndStatusNotIn(product, List.of(EStatus.DONE,EStatus.CANCELED))
                .orElse(Collections.emptyList());
        return !purchaseList.isEmpty();
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
        Purchase purchase = new Purchase();
        purchase.setProduct(stockLevel.getProduct());
        purchase.setQuantity(new BigDecimal(purchaseTaskRequest.getQuantity()));
        purchase.setStatus(EStatus.WAITING);

        Task task = taskRepository.findByName(ETask.TASK_PURCHASE)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));
        purchase.setAssignedUser(task.getDefaultUser());

        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
        purchase.setRequestingUser(user);

        LocalDateTime currentDate = LocalDateTime.now();

        purchase.setCreationDate(currentDate);
        purchaseRepository.save(purchase);

        String purchaseNumber = PURCHASE_PREFIX + PURCHASE_SEPARATOR + currentDate.getDayOfMonth()
                + PURCHASE_SEPARATOR + currentDate.getMonthValue()
                + PURCHASE_SEPARATOR + currentDate.getYear()
                + PURCHASE_SEPARATOR + purchase.getId();

        purchase.setNumber(purchaseNumber);
        purchaseRepository.save(purchase);
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
        List<Purchase> purchaseList = purchaseRepository
                .findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))
                .orElse(Collections.emptyList());
        int total = purchaseList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            delegatedTasksResponse.setTasksList(purchaseTaskListToDelegatedTasksListItem(purchaseList
                    .stream().sorted(Comparator.comparing(Purchase::getCreationDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        delegatedTasksResponse.setTotalTasksLength(total);
    }

    private List<DelegatedTaskListItem> purchaseTaskListToDelegatedTasksListItem(List<Purchase> purchaseList){
        List<DelegatedTaskListItem> delegatedTaskListItems = new ArrayList<>();
        for(Purchase purchase : purchaseList){
            DelegatedTaskListItem delegatedTaskListItem = new DelegatedTaskListItem(purchase.getId(),
                    purchase.getNumber(), purchase.getProduct().getCode(), purchase.getProduct().getName(),
                    purchase.getProduct().getUnit(), purchase.getQuantity().toString(), purchase.getStatus(),
                    purchase.getRequestingUser().getName() + " " +  purchase.getRequestingUser().getSurname(),
                    purchase.getRequestingUser().getId(),
                    purchase.getAssignedUser().getName() + " " +  purchase.getAssignedUser().getSurname(),
                    purchase.getAssignedUser().getId(),
                    purchase.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    findProductStockQuantity(purchase.getProduct()).toString());
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
        Purchase purchase = purchaseRepository.findById(purchaseTaskRequest.getId())
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));
        if(!purchase.getStatus().equals(EStatus.WAITING)){
            throw new ApiExpectationFailedException("exception.taskNotWaiting");
        }
        purchase.setQuantity(new BigDecimal(purchaseTaskRequest.getQuantity()));
        purchase.setUpdateDate(LocalDateTime.now());
        purchaseRepository.save(purchase);
    }

    public void deleteTask(EType type, Long id) {
        if(type.equals(EType.BOUGHT)){
            deleteDelegatedPurchaseTasks(id);
        } else if (type.equals(EType.PRODUCED)){
            deleteDelegatedProductionTasks(id);
        }
    }

    private void deleteDelegatedPurchaseTasks(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));
        if(!purchase.getStatus().equals(EStatus.WAITING)){
            throw new ApiExpectationFailedException("exception.taskNotWaiting");
        }
        purchaseRepository.delete(purchase);
    }

    // TODO
    private void deleteDelegatedProductionTasks(Long id) {
    }

    public ReleasesAcceptancesResponse loadReleases(EStatus status, int page, int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = new ReleasesAcceptancesResponse();
        List<Release> releaseList = this.releaseRepository.findByStatus(status).orElse(Collections.emptyList());
        int total = releaseList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            releasesAcceptancesResponse.setReleasesList(releaseListToReleaseListItem(releaseList.stream()
                    .sorted(Comparator.comparing(Release::getCreationDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        releasesAcceptancesResponse.setTotalTasksLength(total);
        return releasesAcceptancesResponse;
    }

    public ReleasesAcceptancesResponse loadReleases(EStatus status, EDirection direction, int page, int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = new ReleasesAcceptancesResponse();
        List<Release> releaseList = this.releaseRepository.findByStatusAndDirection(status, direction)
                .orElse(Collections.emptyList());
        int total = releaseList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            releasesAcceptancesResponse.setReleasesList(releaseListToReleaseListItem(releaseList.stream()
                    .sorted(Comparator.comparing(Release::getCreationDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        releasesAcceptancesResponse.setTotalTasksLength(total);
        return releasesAcceptancesResponse;
    }

    private List<ReleaseAcceptanceListItem> releaseListToReleaseListItem(List<Release> releases){
        List<ReleaseAcceptanceListItem> releaseListItemAcceptanceList = new ArrayList<>();
        for(Release release: releases){
            ReleaseAcceptanceListItem releaseAcceptanceListItem = new ReleaseAcceptanceListItem(release.getId(), release.getNumber(),
                    release.getOrder().getNumber(),
                    release.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    release.getDirection(), release.getRequestingUser().getId(),
                    release.getRequestingUser().getName() + " " + release.getRequestingUser().getSurname(),
                    release.getAssignedUser().getName() + " " + release.getAssignedUser().getSurname(),
                    release.getAssignedUser().getId(), release.getStatus());
            releaseListItemAcceptanceList.add(releaseAcceptanceListItem);
        }
        return releaseListItemAcceptanceList;
    }

    @Transactional
    public void updateAssignedUsers(UpdateAssignedUserRequest updateAssignedUserRequest) {
        User user = userRepository.findById(updateAssignedUserRequest.getEmployeeId())
                .orElseThrow(() -> new ApiNotFoundException("exception.userNotFound"));
        if(updateAssignedUserRequest.getTask().equals(ETask.TASK_EXTERNAL_RELEASE)
                || updateAssignedUserRequest.getTask().equals(ETask.TASK_INTERNAL_RELEASE)) {
            for (Long id : updateAssignedUserRequest.getTaskIds()) {
                Release release = releaseRepository.findById(id)
                        .orElseThrow(() -> new ApiNotFoundException("exception.releaseNotFound"));
                release.setAssignedUser(user);
                release.setUpdateDate(LocalDateTime.now());
                releaseRepository.save(release);
            }
        } else {
            for (Long id : updateAssignedUserRequest.getTaskIds()) {
                Acceptance acceptance = acceptanceRepository.findById(id)
                        .orElseThrow(() -> new ApiNotFoundException("exception.acceptanceNotFound"));
                acceptance.setAssignedUser(user);
                acceptance.setUpdateDate(LocalDateTime.now());
                acceptanceRepository.save(acceptance);
            }
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
        ReleaseDetails releaseDetails;
        if(release.getDirection().equals(EDirection.EXTERNAL)) {
            releaseDetails = new ReleaseDetails(release.getId(), release.getNumber(),
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
        } else {
            // TODO change when production will be set
            releaseDetails = new ReleaseDetails(release.getId(), release.getNumber(),
                    "production number",
                    release.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    release.getExecutionDate() != null ?
                            release.getExecutionDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "",
                    release.getDirection(), release.getRequestingUser().getName(),
                    release.getRequestingUser().getSurname(), release.getRequestingUser().getEmail(),
                    release.getRequestingUser().getPhone());
        }
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

    @Transactional
    public void markAcceptanceAsInProgress(List<Long> ids) {
        for(Long id: ids){
            Acceptance acceptance = acceptanceRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.acceptanceNotFound"));
            acceptance.setStatus(EStatus.IN_PROGRESS);
            acceptance.setUpdateDate(LocalDateTime.now());
            acceptanceRepository.save(acceptance);
        }
    }

    @Transactional
    public void markAcceptanceAsDone(List<Long> ids) {
        for(Long id: ids){
            Acceptance acceptance = acceptanceRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.acceptanceNotFound"));
            increaseStockLevel(acceptance);
            acceptance.setStatus(EStatus.DONE);
            LocalDateTime currentDate = LocalDateTime.now();
            acceptance.setUpdateDate(currentDate);
            acceptance.setExecutionDate(currentDate);
            acceptanceRepository.save(acceptance);
        }
    }

    private void increaseStockLevel(Acceptance acceptance){
        StockLevel stockLevel = stockLevelRepository.findByProduct(acceptance.getPurchase().getProduct())
                .orElseThrow(() -> new ApiNotFoundException("exception.stockLevelNotFound"));
        BigDecimal newQuantity = stockLevel.getQuantity().add(acceptance.getPurchase().getQuantity());
        stockLevel.setQuantity(newQuantity);
        stockLevelRepository.save(stockLevel);
    }

    public ReleasesAcceptancesResponse loadAcceptances(EStatus status, int page, int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = new ReleasesAcceptancesResponse();
        List<Acceptance> acceptanceList = acceptanceRepository.findByStatus(status).orElse(Collections.emptyList());
        int total = acceptanceList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            releasesAcceptancesResponse.setReleasesList(acceptanceListToAcceptanceListItem(acceptanceList.stream()
                    .sorted(Comparator.comparing(Acceptance::getCreationDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        releasesAcceptancesResponse.setTotalTasksLength(total);
        return releasesAcceptancesResponse;
    }

    public ReleasesAcceptancesResponse loadAcceptances(EStatus status, EDirection direction, int page, int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = new ReleasesAcceptancesResponse();
        List<Acceptance> acceptanceList = acceptanceRepository.findByStatusAndDirection(status, direction)
                .orElse(Collections.emptyList());
        int total = acceptanceList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            releasesAcceptancesResponse.setReleasesList(acceptanceListToAcceptanceListItem(acceptanceList.stream()
                    .sorted(Comparator.comparing(Acceptance::getCreationDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        releasesAcceptancesResponse.setTotalTasksLength(total);
        return releasesAcceptancesResponse;
    }

    private List<ReleaseAcceptanceListItem> acceptanceListToAcceptanceListItem(List<Acceptance> acceptances) {
        List<ReleaseAcceptanceListItem> releaseListItemAcceptanceList = new ArrayList<>();
        for(Acceptance acceptance: acceptances){
            ReleaseAcceptanceListItem releaseAcceptanceListItem = new ReleaseAcceptanceListItem(acceptance.getId(),
                    acceptance.getNumber(), acceptance.getPurchase().getNumber(),
                    acceptance.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    acceptance.getDirection(), acceptance.getRequestingUser().getId(),
                    acceptance.getRequestingUser().getName() + " " + acceptance.getRequestingUser().getSurname(),
                    acceptance.getAssignedUser().getName() + " " + acceptance.getAssignedUser().getSurname(),
                    acceptance.getAssignedUser().getId(), acceptance.getStatus());
            releaseListItemAcceptanceList.add(releaseAcceptanceListItem);
        }
        return releaseListItemAcceptanceList;
    }

    public AcceptanceDetails getAcceptance(Long id) {
        Acceptance acceptance = acceptanceRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.acceptanceNotFound"));
        AcceptanceDetails acceptanceDetails;
        if(acceptance.getDirection().equals(EDirection.EXTERNAL)){
            if(acceptance.getPurchase().getProduct().getContractor() != null) {
                acceptanceDetails = new AcceptanceDetails(acceptance.getId(), acceptance.getNumber(),
                        acceptance.getPurchase().getNumber(), acceptance.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        acceptance.getOrderNumber(), acceptance.getExecutionDate() != null ?
                        acceptance.getExecutionDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "",
                        acceptance.getDirection(), acceptance.getPurchase().getProduct().getContractor().getName(),
                        acceptance.getPurchase().getProduct().getContractor().getCountry(),
                        acceptance.getPurchase().getProduct().getContractor().getNip(),
                        acceptance.getPurchase().getProduct().getContractor().getBankAccount(),
                        acceptance.getPurchase().getProduct().getContractor().getAccountNumber(),
                        acceptance.getPurchase().getProduct().getContractor().getUrl(),
                        acceptance.getPurchase().getProduct().getContractor().getEmail(),
                        acceptance.getPurchase().getProduct().getContractor().getPhone(),
                        acceptance.getPurchase().getProduct().getContractor().getPostalCode(),
                        acceptance.getPurchase().getProduct().getContractor().getPost(),
                        acceptance.getPurchase().getProduct().getContractor().getCity(),
                        acceptance.getPurchase().getProduct().getContractor().getStreet(),
                        acceptance.getPurchase().getProduct().getContractor().getBuildingNumber(),
                        acceptance.getPurchase().getProduct().getContractor().getDoorNumber());
            } else {
                acceptanceDetails = new AcceptanceDetails(acceptance.getId(), acceptance.getNumber(),
                        acceptance.getPurchase().getNumber(), acceptance.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        acceptance.getOrderNumber(), acceptance.getExecutionDate() != null ?
                        acceptance.getExecutionDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "",
                        acceptance.getDirection(), messageSource.getMessage(
                        "message.noContractorData", null, LocaleContextHolder.getLocale()));
            }
            List<ReleaseProductQuantity> releaseProductQuantityList = new ArrayList<>();
            ReleaseProductQuantity releaseProductQuantity = new ReleaseProductQuantity(
                    acceptance.getPurchase().getProduct().getCode(), acceptance.getPurchase().getQuantity().toString());
            releaseProductQuantityList.add(releaseProductQuantity);
            acceptanceDetails.setProductSet(releaseProductQuantityList);
        } else {
            // TODO change when production will be set
            acceptanceDetails = new AcceptanceDetails(acceptance.getId(), acceptance.getNumber(),
                    "production number", acceptance.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    acceptance.getExecutionDate() != null ?
                    acceptance.getExecutionDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "",
                    acceptance.getDirection(), acceptance.getRequestingUser().getName(), acceptance.getRequestingUser().getSurname(),
                    acceptance.getRequestingUser().getEmail(), acceptance.getRequestingUser().getPhone());
        }
        return acceptanceDetails;
    }
}
