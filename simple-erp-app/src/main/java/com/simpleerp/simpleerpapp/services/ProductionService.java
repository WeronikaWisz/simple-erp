package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.manageusers.UserName;
import com.simpleerp.simpleerpapp.dtos.production.ProductProductionInfo;
import com.simpleerp.simpleerpapp.dtos.production.ProductionProductListItem;
import com.simpleerp.simpleerpapp.dtos.production.ProductionProductQuantity;
import com.simpleerp.simpleerpapp.dtos.production.ProductionProductResponse;
import com.simpleerp.simpleerpapp.dtos.products.*;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductionService {

    private static final String RELEASE_PREFIX = "WW";
    private static final String ACCEPTANCE_PREFIX = "PW";
    private static final String SEPARATOR = "/";

    private final ProductRepository productRepository;
    private final ProductionRepository productionRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ReleaseRepository releaseRepository;
    private final AcceptanceRepository acceptanceRepository;
    private final ProductProductionRepository productProductionRepository;
    private final MessageSource messageSource;

    @Autowired
    public ProductionService(ProductRepository productRepository, ProductionRepository productionRepository,
                             UserRepository userRepository, TaskRepository taskRepository,
                             ReleaseRepository releaseRepository, AcceptanceRepository acceptanceRepository,
                             MessageSource messageSource, ProductProductionRepository productProductionRepository) {
        this.productRepository = productRepository;
        this.productionRepository = productionRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.releaseRepository = releaseRepository;
        this.acceptanceRepository = acceptanceRepository;
        this.productProductionRepository = productProductionRepository;
        this.messageSource = messageSource;
    }

    public DelegatedTasksResponse loadProductionTasks(EStatus status, int page, int size) {
        DelegatedTasksResponse delegatedTasksResponse = new DelegatedTasksResponse();
        List<Production> productionList = productionRepository.findByStatus(status);
        int total = productionList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            delegatedTasksResponse.setTasksList(productionTaskListToDelegatedTasksListItem(productionList
                    .stream().sorted(Comparator.comparing(Production::getCreationDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        delegatedTasksResponse.setTotalTasksLength(total);
        return delegatedTasksResponse;
    }

    private List<DelegatedTaskListItem> productionTaskListToDelegatedTasksListItem(List<Production> productionList){
        List<DelegatedTaskListItem> delegatedTaskListItems = new ArrayList<>();
        for(Production production : productionList){

            String orderNumber = "";
            boolean accepted = false;
            if(production.getStatus().equals(EStatus.WAITING)) {
                Optional<Release> release = releaseRepository.findByProduction(production);
                if (release.isPresent()) {
                    orderNumber = release.get().getNumber();
                    accepted = release.get().getStatus().equals(EStatus.DONE);
                }
            } else if (production.getStatus().equals(EStatus.IN_PROGRESS)){
                Optional<Acceptance> acceptance = acceptanceRepository.findByProduction(production);
                if (acceptance.isPresent()) {
                    orderNumber = acceptance.get().getNumber();
                    accepted = acceptance.get().getStatus().equals(EStatus.DONE);
                }
            }

            DelegatedTaskListItem delegatedTaskListItem = new DelegatedTaskListItem(production.getId(),
                    production.getNumber(), production.getProduct().getCode(), production.getProduct().getName(),
                    production.getProduct().getUnit(), production.getQuantity().toString(), production.getStatus(),
                    production.getRequestingUser().getName() + " " +  production.getRequestingUser().getSurname(),
                    production.getRequestingUser().getId(),
                    production.getAssignedUser().getName() + " " +  production.getAssignedUser().getSurname(),
                    production.getAssignedUser().getId(),
                    production.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    orderNumber, accepted);
            delegatedTaskListItems.add(delegatedTaskListItem);
        }
        return delegatedTaskListItems;
    }

    @Transactional
    public void markProductionAsInProgress(List<Long> ids) {
        for(Long id: ids){
            Production production = productionRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.productionNotFound"));
            production.setStatus(EStatus.IN_PROGRESS);
            production.setUpdateDate(LocalDateTime.now());
            productionRepository.save(production);
        }
    }

    @Transactional
    public void markProductionAsDone(List<Long> ids) {
        for(Long id: ids){
            Production production = productionRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.productionNotFound"));
            production.setStatus(EStatus.DONE);
            LocalDateTime currentDate = LocalDateTime.now();
            production.setUpdateDate(currentDate);
            production.setExecutionDate(currentDate);
            productionRepository.save(production);
        }
    }

    @Transactional
    public void delegateInternalRelease(List<Long> ids) {
        for(Long id: ids){
            Production production = productionRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.productionNotFound"));
            this.createInternalRelease(production);
            production.setUpdateDate(LocalDateTime.now());
            productionRepository.save(production);
        }
    }

    private void createInternalRelease(Production production) {
        Task task = taskRepository.findByName(ETask.TASK_INTERNAL_RELEASE)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));

        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));

        LocalDateTime currentDate = LocalDateTime.now();

        Release release = releaseRepository.save(new Release(production, user, task.getDefaultUser(),
                EDirection.INTERNAL, currentDate, EStatus.WAITING));

        String releaseNumber = RELEASE_PREFIX + SEPARATOR + currentDate.getDayOfMonth()
                + SEPARATOR + currentDate.getMonthValue()
                + SEPARATOR + currentDate.getYear()
                + SEPARATOR + release.getId();

        release.setNumber(releaseNumber);
        releaseRepository.save(release);
    }

    @Transactional
    public void delegateInternalAcceptance(List<Long> ids) {
        for(Long id: ids){
            Production production = productionRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.productionNotFound"));
            this.createInternalAcceptance(production);
            production.setUpdateDate(LocalDateTime.now());
            productionRepository.save(production);
        }
    }

    private void createInternalAcceptance(Production production) {
        Task task = taskRepository.findByName(ETask.TASK_INTERNAL_ACCEPTANCE)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));

        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));

        LocalDateTime currentDate = LocalDateTime.now();

        Acceptance acceptance = acceptanceRepository.save(new Acceptance(production, user,
                task.getDefaultUser(), EDirection.INTERNAL, currentDate, EStatus.WAITING));

        String acceptanceNumber = ACCEPTANCE_PREFIX + SEPARATOR + currentDate.getDayOfMonth()
                + SEPARATOR + currentDate.getMonthValue()
                + SEPARATOR + currentDate.getYear()
                + SEPARATOR + acceptance.getId();
        acceptance.setNumber(acceptanceNumber);
        acceptanceRepository.save(acceptance);

    }

    public List<UserName> loadUsers() {
        List<User> userList = userRepository.findByIsDeletedFalse()
                .stream().filter(user -> user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                        .contains(ERole.ROLE_PRODUCTION)).collect(Collectors.toList());
        Optional<User> admin = userRepository.findByUsername("admin");
        admin.ifPresent(userList::add);
        List<UserName> userNameList = new ArrayList<>();
        for (User user: userList){
            userNameList.add(new UserName(user.getId(), user.getName() + " " + user.getSurname()));
        }
        return userNameList;
    }

    @Transactional
    public void updateAssignedUser(UpdateAssignedUserRequest updateAssignedUserRequest) {
        User user = userRepository.findById(updateAssignedUserRequest.getEmployeeId())
                .orElseThrow(() -> new ApiNotFoundException("exception.userNotFound"));
        if(user.getIsDeleted()){
            throw new ApiExpectationFailedException("exception.userDeleted");
        }
        if(updateAssignedUserRequest.getTask().equals(ETask.TASK_PRODUCTION)) {
            for (Long id : updateAssignedUserRequest.getTaskIds()) {
                Production production = productionRepository.findById(id)
                        .orElseThrow(() -> new ApiNotFoundException("exception.productionNotFound"));
                production.setAssignedUser(user);
                production.setUpdateDate(LocalDateTime.now());
                productionRepository.save(production);
            }
        }
    }

    private String getCurrentUserUsername(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }

    public ReleasesAcceptancesResponse loadDelegatedTasks(ETask task, int page, int size) {
        if(task.equals(ETask.TASK_INTERNAL_RELEASE)){
            return loadReleases(page, size);
        } else {
            return loadAcceptances(page, size);
        }
    }

    public ReleasesAcceptancesResponse loadReleases(int page, int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = new ReleasesAcceptancesResponse();
        List<Release> releaseList = this.releaseRepository.findByStatusInAndDirection(List.of(EStatus.WAITING, EStatus.IN_PROGRESS),
                        EDirection.INTERNAL);
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
                    release.getProduction().getNumber(),
                    release.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    release.getDirection(), release.getRequestingUser().getId(),
                    release.getRequestingUser().getName() + " " + release.getRequestingUser().getSurname(),
                    release.getAssignedUser().getName() + " " + release.getAssignedUser().getSurname(),
                    release.getAssignedUser().getId(), release.getStatus());
            releaseListItemAcceptanceList.add(releaseAcceptanceListItem);
        }
        return releaseListItemAcceptanceList;
    }

    public ReleasesAcceptancesResponse loadAcceptances(int page, int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = new ReleasesAcceptancesResponse();
        List<Acceptance> acceptanceList = acceptanceRepository.findByStatusInAndDirection(List.of(EStatus.WAITING, EStatus.IN_PROGRESS),
                        EDirection.INTERNAL);
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
                    acceptance.getNumber(), acceptance.getProduction().getNumber(),
                    acceptance.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    acceptance.getDirection(), acceptance.getRequestingUser().getId(),
                    acceptance.getRequestingUser().getName() + " " + acceptance.getRequestingUser().getSurname(),
                    acceptance.getAssignedUser().getName() + " " + acceptance.getAssignedUser().getSurname(),
                    acceptance.getAssignedUser().getId(), acceptance.getStatus());
            releaseListItemAcceptanceList.add(releaseAcceptanceListItem);
        }
        return releaseListItemAcceptanceList;
    }

    public List<ProductCode> loadProductList() {
        List<Product> productList = productRepository.findByIsDeletedFalse();
        List<ProductCode> productCodeList = new ArrayList<>();
        for (Product product: productList){
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
                release.getProduction().getNumber(),
                release.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                release.getExecutionDate() != null ?
                        release.getExecutionDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "",
                release.getDirection(), release.getRequestingUser().getName(),
                release.getRequestingUser().getSurname(), release.getRequestingUser().getEmail(),
                release.getRequestingUser().getPhone());
        List<ReleaseProductQuantity> releaseProductQuantityList = new ArrayList<>();
        ProductProduction productProduction = productProductionRepository.findByProductCode(
                        release.getProduction().getProduct().getCode())
                .orElseThrow(() -> new ApiNotFoundException("exception.productProductionNotFound"));
        for(ProductProductionProducts productProductionProduct: productProduction.getProductProductionProducts()){
            ReleaseProductQuantity releaseProductQuantity = new ReleaseProductQuantity(
                    productProductionProduct.getProduct().getCode(),
                    (productProductionProduct.getQuantity().multiply(release.getProduction().getQuantity()))
                            .setScale(2, RoundingMode.HALF_UP).toString(),
                    true);
            releaseProductQuantityList.add(releaseProductQuantity);
        }
        releaseDetails.setProductSet(releaseProductQuantityList);
        return releaseDetails;
    }

    public AcceptanceDetails getAcceptance(Long id) {
        Acceptance acceptance = acceptanceRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.acceptanceNotFound"));
        AcceptanceDetails acceptanceDetails = new AcceptanceDetails(acceptance.getId(), acceptance.getNumber(),
                acceptance.getProduction().getNumber(), acceptance.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                acceptance.getExecutionDate() != null ?
                        acceptance.getExecutionDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "",
                acceptance.getDirection(), acceptance.getRequestingUser().getName(), acceptance.getRequestingUser().getSurname(),
                acceptance.getRequestingUser().getEmail(), acceptance.getRequestingUser().getPhone());
        List<ReleaseProductQuantity> releaseProductQuantityList = new ArrayList<>();
        ReleaseProductQuantity releaseProductQuantity = new ReleaseProductQuantity(
                acceptance.getProduction().getProduct().getCode(), acceptance.getProduction().getQuantity().toString());
        releaseProductQuantityList.add(releaseProductQuantity);
        acceptanceDetails.setProductSet(releaseProductQuantityList);
        return acceptanceDetails;
    }

    public ProductProductionInfo getProductInfo(Long id) {
        Product product = productRepository.findById(id).
                orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
        return getProductProduction(product);
    }

    public ProductProductionInfo getProductionInfo(Long id) {
        Production production = productionRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.productionNotFound"));
        return getProductProduction(production.getProduct());
    }

    private ProductProductionInfo getProductProduction(Product product){
        ProductProductionInfo productProductionInfo = new ProductProductionInfo(product.getCode(),
                product.getName(), product.getUnit());
        List<ProductionProductQuantity> productQuantityList = new ArrayList<>();
        ProductProduction productProduction = productProductionRepository.findByProductCode(
                product.getCode()).orElseThrow(() -> new ApiNotFoundException("exception.productProductionNotFound"));
        for(ProductProductionProducts productProductionProduct: productProduction.getProductProductionProducts()){
            ProductionProductQuantity productionProductQuantity = new ProductionProductQuantity(
                    productProductionProduct.getProduct().getCode(),
                    productProductionProduct.getQuantity().toString());
            productQuantityList.add(productionProductQuantity);
        }
        productProductionInfo.setProductSet(productQuantityList);

        List<ProductStepDescription> productStepDescriptionList = new ArrayList<>();
        for(ProductionStep productionStep: productProduction.getProductionSteps()){
            ProductStepDescription productStepDescription = new ProductStepDescription(productionStep.getDescription());
            productStepDescriptionList.add(productStepDescription);
        }
        productProductionInfo.setProductionSteps(productStepDescriptionList);
        return productProductionInfo;
    }

    public ProductionProductResponse loadProductionProducts(int page, int size) {
        ProductionProductResponse productionProductResponse = new ProductionProductResponse();
        List<Product> productList = productRepository.findByTypeAndIsDeletedFalse(EType.PRODUCED);
        int total = productList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            productionProductResponse.setProductsList(productListToProductListItem(productList).stream()
                    .sorted(Comparator.comparing(ProductionProductListItem::getCode))
                    .collect(Collectors.toList()).subList(start, end));
        }
        productionProductResponse.setTotalProductsLength(total);
        return productionProductResponse;
    }

    private List<ProductionProductListItem> productListToProductListItem(List<Product> productList) {
        List<ProductionProductListItem> productionProductListItems = new ArrayList<>();
        for(Product product: productList){
            ProductionProductListItem productionProductListItem = new ProductionProductListItem(product.getId(),
                   product.getCode(), product.getName(), product.getUnit());
            productionProductListItems.add(productionProductListItem);
        }
        return productionProductListItems;
    }
}
