package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.manageusers.UserName;
import com.simpleerp.simpleerpapp.dtos.products.ProductCode;
import com.simpleerp.simpleerpapp.dtos.trade.*;
import com.simpleerp.simpleerpapp.dtos.warehouse.DelegatedTaskListItem;
import com.simpleerp.simpleerpapp.dtos.warehouse.DelegatedTasksResponse;
import com.simpleerp.simpleerpapp.enums.*;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.*;
import com.simpleerp.simpleerpapp.repositories.*;
import com.simpleerp.simpleerpapp.security.userdetails.UserDetailsI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TradeService {

    private static final String RELEASE_PREFIX = "WZ";
    private static final String ACCEPTANCE_PREFIX = "PZ";
    private static final String SEPARATOR = "/";

    private final ProductRepository productRepository;
    private final ProductSetRepository productSetRepository;
    private final OrderRepository orderRepository;
    private final OrderProductsRepository orderProductsRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ReleaseRepository releaseRepository;
    private final PurchaseRepository purchaseRepository;
    private final AcceptanceRepository acceptanceRepository;

    @Autowired
    public TradeService(ProductRepository productRepository, ProductSetRepository productSetRepository,
                        OrderRepository orderRepository, OrderProductsRepository orderProductsRepository,
                        CustomerRepository customerRepository, UserRepository userRepository,
                        TaskRepository taskRepository, ReleaseRepository releaseRepository,
                        PurchaseRepository purchaseRepository, AcceptanceRepository acceptanceRepository) {
        this.productRepository = productRepository;
        this.productSetRepository = productSetRepository;
        this.orderRepository = orderRepository;
        this.orderProductsRepository = orderProductsRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.releaseRepository = releaseRepository;
        this.purchaseRepository = purchaseRepository;
        this.acceptanceRepository = acceptanceRepository;
    }

    public List<ProductCode> loadProductList() {
        List<Product> productList = productRepository.findAll();
        List<ProductSet> productSetList = productSetRepository.findAll();
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
        for(ProductSet productSet: productSetList){
            ProductCode productCode = new ProductCode();
            productCode.setId(productSet.getId());
            productCode.setName(productSet.getName());
            productCode.setCode(productSet.getCode());
            productCode.setUnit(EUnit.PIECES);
            productCodeList.add(productCode);
        }
        return productCodeList;
    }

    @Transactional
    public void addOrder(AddOrderRequest addOrderRequest) {
        Optional<Order> existingOrder = orderRepository.findByNumber(addOrderRequest.getNumber());
        if(existingOrder.isPresent()){
            throw new ApiExpectationFailedException("exception.orderNumberAlreadyUsed");
        }

        Customer customer = findOrCreateCustomer(new CustomerData(addOrderRequest.getName(), addOrderRequest.getSurname(),
                addOrderRequest.getEmail(), addOrderRequest.getPhone(), addOrderRequest.getPostalCode(),
                addOrderRequest.getPost(), addOrderRequest.getCity(), addOrderRequest.getStreet(),
                addOrderRequest.getBuildingNumber(), addOrderRequest.getDoorNumber()));

        Task task = taskRepository.findByName(ETask.TASK_SALE)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));

        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));

        OrderProductList orderProductList = new OrderProductList();
        orderProductList.setOrderProductQuantityList(addOrderRequest.getProductSet());

        Order order = orderRepository.save(new Order(addOrderRequest.getNumber(), addOrderRequest.getOrderDate(),
                EStatus.WAITING, customer, LocalDateTime.now(), user, task.getDefaultUser(),
                orderProductList));

        BigDecimal calculatedPrice = new BigDecimal(0);
        Map<Product, BigDecimal> orderProductsMap = new HashMap<>();
        for(OrderProductQuantity orderProductQuantity: addOrderRequest.getProductSet()){
            Optional<Product> product = productRepository.findByCode(orderProductQuantity.getProduct());
            Optional<ProductSet> productSet = productSetRepository.findByCode(orderProductQuantity.getProduct());
            if(product.isEmpty() && productSet.isEmpty()){
                throw new ApiExpectationFailedException("exception.productNotFound");
            }
            if(product.isPresent()){
                orderProductsMap.put(product.get(), orderProductsMap.getOrDefault(product.get(), new BigDecimal(0))
                        .add(new BigDecimal(orderProductQuantity.getQuantity())));
                calculatedPrice = calculatedPrice.add(product.get().getSalePrice().multiply(
                        new BigDecimal(orderProductQuantity.getQuantity())));
            } else {
                List<ProductSetProducts> productSetProducts = productSet.get().getProductsSets();
                for(ProductSetProducts productSetProduct: productSetProducts){
                    orderProductsMap.put(productSetProduct.getProduct(),
                            orderProductsMap.getOrDefault(productSetProduct.getProduct(), new BigDecimal(0))
                            .add((new BigDecimal(orderProductQuantity.getQuantity()))
                                    .multiply(productSetProduct.getQuantity())));
                }
                calculatedPrice = calculatedPrice.add(productSet.get().getSalePrice().multiply(
                        new BigDecimal(orderProductQuantity.getQuantity())));
            }

        }

        orderProductsMap.forEach((k,v) -> orderProductsRepository.save(new OrderProducts(order, k, v)));

        order.setCalculatedPrice(calculatedPrice);
        BigDecimal totalPrice;
        if(addOrderRequest.getPrice() != null && !addOrderRequest.getPrice().isEmpty()){
            totalPrice = new BigDecimal(addOrderRequest.getPrice());
            order.setPrice(new BigDecimal(addOrderRequest.getPrice()));
        } else {
            totalPrice = calculatedPrice;
            order.setPrice(calculatedPrice);
        }
        if(addOrderRequest.getDiscount() != null && !addOrderRequest.getDiscount().isEmpty()){
            totalPrice = totalPrice.subtract(totalPrice.multiply(
                    (new BigDecimal(addOrderRequest.getDiscount()))
                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));
            order.setDiscount(new BigDecimal(addOrderRequest.getDiscount()));
        }
        if(addOrderRequest.getDelivery() != null && !addOrderRequest.getDelivery().isEmpty()){
            totalPrice = totalPrice.add(new BigDecimal(addOrderRequest.getDelivery()));
            order.setDelivery(new BigDecimal(addOrderRequest.getDelivery()));
        }
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
    }

    private String getCurrentUserUsername(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }

    private Customer findOrCreateCustomer(CustomerData customerData){
        Optional<Customer> customer = customerRepository.findByEmail(customerData.getEmail());

        String phoneNumber = customerData.getPhone();
        if(!Objects.equals(phoneNumber, "")) {
            phoneNumber = phoneNumber.replaceAll("\\s+", "");
            if (!phoneNumber.startsWith("+48")) {
                phoneNumber = "+48" + phoneNumber;
            }
        }

        if(customer.isPresent()){
            boolean hasChanged = false;
            if(!Objects.equals(customer.get().getName(), customerData.getName())){
                customer.get().setName(customerData.getName());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getSurname(), customerData.getSurname())){
                customer.get().setSurname(customerData.getSurname());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getPhone(), phoneNumber)){
                customer.get().setPhone(phoneNumber);
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getPostalCode(), customerData.getPostalCode())){
                customer.get().setPostalCode(customerData.getPostalCode());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getPost(), customerData.getPost())){
                customer.get().setPost(customerData.getPost());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getCity(), customerData.getCity())){
                customer.get().setCity(customerData.getCity());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getStreet(), customerData.getStreet())){
                customer.get().setStreet(customerData.getStreet());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getBuildingNumber(), customerData.getBuildingNumber())){
                customer.get().setBuildingNumber(customerData.getBuildingNumber());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getDoorNumber(), customerData.getDoorNumber())){
                customer.get().setDoorNumber(customerData.getDoorNumber());
                hasChanged = true;
            }
            if(hasChanged){
                customer.get().setUpdateDate(LocalDateTime.now());
            }
            return customer.get();
        } else {
            return customerRepository.save(new Customer(customerData.getName(), customerData.getSurname(),
                    customerData.getEmail(), phoneNumber, customerData.getPostalCode(),
                    customerData.getPost(), customerData.getCity(), customerData.getStreet(),
                    customerData.getBuildingNumber(), customerData.getDoorNumber(), LocalDateTime.now()));
        }
    }

    public OrdersResponse loadOrders(EStatus status, int page, int size) {
        OrdersResponse ordersResponse = new OrdersResponse();
        List<Order> orderList = orderRepository.findByStatus(status).orElse(Collections.emptyList());
        int total = orderList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            ordersResponse.setOrdersList(orderListToOrdersListItem(orderList.stream()
                    .sorted(Comparator.comparing(Order::getOrderDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        ordersResponse.setTotalOrdersLength(total);
        return ordersResponse;
    }

    private List<OrderListItem> orderListToOrdersListItem(List<Order> orders) {
        List<OrderListItem> orderListItemList = new ArrayList<>();
        for(Order order: orders){
            boolean isIssued = checkIfOrderIssued(order);
            OrderListItem orderListItem = new OrderListItem(order.getId(), order.getNumber(),
                    order.getOrderDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    order.getTotalPrice().toString(), order.getCustomer().getId(),
                    order.getCustomer().getName() + " " + order.getCustomer().getSurname(),
                    order.getAssignedUser().getName() + " " + order.getAssignedUser().getSurname(),
                    order.getAssignedUser().getId(), order.getStatus(), isIssued);
            orderListItemList.add(orderListItem);
        }
        return orderListItemList;
    }

    private boolean checkIfOrderIssued(Order order) {
        Optional<Release> release = releaseRepository.findByOrder(order);
        return release.isPresent() && release.get().getStatus().equals(EStatus.DONE);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.orderNotFound"));
        if(!order.getStatus().equals(EStatus.WAITING)){
            throw new ApiExpectationFailedException("exception.orderNotWaiting");
        }
        List<OrderProducts> orderProducts = order.getOrderProductsSet();
        orderProductsRepository.deleteAll(orderProducts);
        orderRepository.delete(order);
    }

    public List<UserName> loadUsers() {
        List<User> userList = userRepository.findAll()
                .stream().filter(user -> user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                        .contains(ERole.ROLE_TRADE)).collect(Collectors.toList());
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
        if(updateAssignedUserRequest.getTask().equals(ETask.TASK_SALE)) {
            for (Long id : updateAssignedUserRequest.getTaskIds()) {
                Order order = orderRepository.findById(id)
                        .orElseThrow(() -> new ApiNotFoundException("exception.orderNotFound"));
                order.setAssignedUser(user);
                order.setUpdateDate(LocalDateTime.now());
                orderRepository.save(order);
            }
        }
        if(updateAssignedUserRequest.getTask().equals(ETask.TASK_PURCHASE)) {
            for (Long id : updateAssignedUserRequest.getTaskIds()) {
                Purchase purchase = purchaseRepository.findById(id)
                        .orElseThrow(() -> new ApiNotFoundException("exception.purchaseNotFound"));
                purchase.setAssignedUser(user);
                purchase.setUpdateDate(LocalDateTime.now());
                purchaseRepository.save(purchase);
            }
        }
    }

    public UpdateOrderRequest getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.orderNotFound"));
        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest(order.getId(), order.getNumber(),
                order.getOrderDate(), order.getDiscount() != null ? order.getDiscount().toString() : "",
                order.getDelivery() != null ? order.getDelivery().toString() : "",
                order.getPrice() != null ? order.getPrice().toString() : "",
                order.getCustomer().getName(), order.getCustomer().getSurname(), order.getCustomer().getEmail(),
                order.getCustomer().getPhone(), order.getCustomer().getPostalCode(), order.getCustomer().getPost(),
                order.getCustomer().getCity(), order.getCustomer().getStreet(), order.getCustomer().getBuildingNumber(),
                order.getCustomer().getDoorNumber());
        updateOrderRequest.setProductSet(order.getOrderProducts().getOrderProductQuantityList());
        return updateOrderRequest;
    }

    @Transactional
    public void updateOrder(UpdateOrderRequest updateOrderRequest) {
        Order order = orderRepository.findById(updateOrderRequest.getId())
                .orElseThrow(() -> new ApiNotFoundException("exception.orderNotFound"));
        if(!Objects.equals(order.getNumber(), updateOrderRequest.getNumber())){
            Optional<Order> existingOrder = orderRepository.findByNumber(updateOrderRequest.getNumber());
            if(existingOrder.isPresent()) {
                throw new ApiExpectationFailedException("exception.orderNumberAlreadyUsed");
            }
            order.setNumber(updateOrderRequest.getNumber());
        }
        order.setOrderDate(updateOrderRequest.getOrderDate());

        Customer customer = findOrCreateCustomer(new CustomerData(updateOrderRequest.getName(), updateOrderRequest.getSurname(),
                updateOrderRequest.getEmail(), updateOrderRequest.getPhone(), updateOrderRequest.getPostalCode(),
                updateOrderRequest.getPost(), updateOrderRequest.getCity(), updateOrderRequest.getStreet(),
                updateOrderRequest.getBuildingNumber(), updateOrderRequest.getDoorNumber()));

        order.setCustomer(customer);

        OrderProductList orderProductList = new OrderProductList();
        orderProductList.setOrderProductQuantityList(updateOrderRequest.getProductSet());
        order.setOrderProducts(orderProductList);

        BigDecimal calculatedPrice = new BigDecimal(0);

        Map<Product, BigDecimal> orderProductsMap = new HashMap<>();
        for(OrderProductQuantity orderProductQuantity: updateOrderRequest.getProductSet()){
            Optional<Product> product = productRepository.findByCode(orderProductQuantity.getProduct());
            Optional<ProductSet> productSet = productSetRepository.findByCode(orderProductQuantity.getProduct());
            if(product.isEmpty() && productSet.isEmpty()){
                throw new ApiExpectationFailedException("exception.productNotFound");
            }
            if(product.isPresent()){
                orderProductsMap.put(product.get(), orderProductsMap.getOrDefault(product.get(), new BigDecimal(0))
                        .add(new BigDecimal(orderProductQuantity.getQuantity())));
                calculatedPrice = calculatedPrice.add(product.get().getSalePrice().multiply(
                        new BigDecimal(orderProductQuantity.getQuantity())));
            } else {
                List<ProductSetProducts> productSetProducts = productSet.get().getProductsSets();
                for(ProductSetProducts productSetProduct: productSetProducts){
                    orderProductsMap.put(productSetProduct.getProduct(),
                            orderProductsMap.getOrDefault(productSetProduct.getProduct(), new BigDecimal(0))
                                    .add((new BigDecimal(orderProductQuantity.getQuantity()))
                                            .multiply(productSetProduct.getQuantity())));
                }
                calculatedPrice = calculatedPrice.add(productSet.get().getSalePrice().multiply(
                        new BigDecimal(orderProductQuantity.getQuantity())));
            }
        }

        List<OrderProducts> orderProducts = order.getOrderProductsSet();

        orderProductsMap.forEach((k,v) -> {
            Optional<OrderProducts> productAlreadyInList = orderProducts.stream()
                    .filter(currentProduct -> currentProduct.getProduct().getId().equals(k.getId())).findFirst();
            if(productAlreadyInList.isPresent()){
                productAlreadyInList.get().setQuantity(v);
                orderProductsRepository.save(productAlreadyInList.get());
            } else {
                orderProductsRepository.save(new OrderProducts(order, k, v));
            }
        });

        List<OrderProducts> orderProductsToDelete = new ArrayList<>();
        for(OrderProducts orderProduct: orderProducts){
            if(!orderProductsMap.containsKey(orderProduct.getProduct())){
                orderProductsToDelete.add(orderProduct);
            }
        }
        orderProductsRepository.deleteAll(orderProductsToDelete);

        order.setCalculatedPrice(calculatedPrice);
        BigDecimal totalPrice;
        if(updateOrderRequest.getPrice() != null && !updateOrderRequest.getPrice().isEmpty()){
            totalPrice = new BigDecimal(updateOrderRequest.getPrice());
            order.setPrice(new BigDecimal(updateOrderRequest.getPrice()));
        } else {
            totalPrice = calculatedPrice;
            order.setPrice(calculatedPrice);
        }
        if(updateOrderRequest.getDiscount() != null && !updateOrderRequest.getDiscount().isEmpty()){
            totalPrice = totalPrice.subtract(totalPrice.multiply(
                    (new BigDecimal(updateOrderRequest.getDiscount()))
                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));
            order.setDiscount(new BigDecimal(updateOrderRequest.getDiscount()));
        }
        if(updateOrderRequest.getDelivery() != null && !updateOrderRequest.getDelivery().isEmpty()){
            totalPrice = totalPrice.add(new BigDecimal(updateOrderRequest.getDelivery()));
            order.setDelivery(new BigDecimal(updateOrderRequest.getDelivery()));
        }
        order.setTotalPrice(totalPrice);
        order.setUpdateDate(LocalDateTime.now());
        orderRepository.save(order);
    }


    @Transactional
    public void delegateExternalRelease(List<Long> ids) {
        for(Long id: ids){
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.orderNotFound"));
            this.createExternalRelease(order);
            order.setStatus(EStatus.IN_PROGRESS);
            order.setUpdateDate(LocalDateTime.now());
            orderRepository.save(order);
        }
    }

    private void createExternalRelease(Order order) {
        Task task = taskRepository.findByName(ETask.TASK_EXTERNAL_RELEASE)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));

        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));

        LocalDateTime currentDate = LocalDateTime.now();

        Release release = releaseRepository.save(new Release(order, user, task.getDefaultUser(),
                EDirection.EXTERNAL, currentDate, EStatus.WAITING));

        String releaseNumber = RELEASE_PREFIX + SEPARATOR + currentDate.getDayOfMonth()
                + SEPARATOR + currentDate.getMonthValue()
                + SEPARATOR + currentDate.getYear()
                + SEPARATOR + release.getId();

        release.setNumber(releaseNumber);
        releaseRepository.save(release);
    }

    @Transactional
    public void markOrderAsReceived(List<Long> ids) {
        for(Long id: ids){
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.orderNotFound"));
            Optional<Release> release = releaseRepository.findByOrder(order);
            if(release.isEmpty() || !release.get().getStatus().equals(EStatus.DONE)){
                throw new ApiExpectationFailedException("exception.releaseNotDone");
            }
            order.setStatus(EStatus.DONE);
            LocalDateTime currentDate = LocalDateTime.now();
            order.setUpdateDate(currentDate);
            order.setExecutionDate(currentDate);
            orderRepository.save(order);
        }
    }

    public DelegatedTasksResponse loadPurchaseTasks(EStatus status, int page, int size) {
        DelegatedTasksResponse delegatedTasksResponse = new DelegatedTasksResponse();
        List<Purchase> purchaseList = purchaseRepository.findByStatus(status)
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
        return delegatedTasksResponse;
    }

    private List<DelegatedTaskListItem> purchaseTaskListToDelegatedTasksListItem(List<Purchase> purchaseList){
        List<DelegatedTaskListItem> delegatedTaskListItems = new ArrayList<>();
        for(Purchase purchase : purchaseList){

            Optional<Acceptance> acceptance = acceptanceRepository.findByPurchaseAndStatusNotIn(purchase,
                    List.of(EStatus.DONE,EStatus.CANCELED));

            String orderNumber = "";
            boolean accepted = false;
            if(acceptance.isPresent() && acceptance.get().getOrderNumber() != null){
                orderNumber = acceptance.get().getOrderNumber();
                accepted = acceptance.get().getStatus().equals(EStatus.DONE);
            }

            DelegatedTaskListItem delegatedTaskListItem = new DelegatedTaskListItem(purchase.getId(),
                    purchase.getNumber(), purchase.getProduct().getCode(), purchase.getProduct().getName(),
                    purchase.getProduct().getUnit(), purchase.getQuantity().toString(), purchase.getStatus(),
                    purchase.getAssignedUser().getName() + " " +  purchase.getAssignedUser().getSurname(),
                    purchase.getAssignedUser().getId(),
                    purchase.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    orderNumber, accepted);
            delegatedTaskListItems.add(delegatedTaskListItem);
        }
        return delegatedTaskListItems;
    }

    @Transactional
    public void delegateExternalAcceptance(DelegateExternalAcceptance delegateExternalAcceptance) {
        for(Long id: delegateExternalAcceptance.getIds()){
            Purchase purchase = purchaseRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.purchaseNotFound"));
            this.createExternalAcceptance(purchase, delegateExternalAcceptance.getOrderNumber());
            purchase.setUpdateDate(LocalDateTime.now());
            purchaseRepository.save(purchase);
        }
    }

    private void createExternalAcceptance(Purchase purchase, String orderNumber) {
        Task task = taskRepository.findByName(ETask.TASK_EXTERNAL_ACCEPTANCE)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));

        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));

        LocalDateTime currentDate = LocalDateTime.now();

        Acceptance acceptance = acceptanceRepository.save(new Acceptance(purchase, orderNumber, user,
                task.getDefaultUser(), EDirection.EXTERNAL, currentDate, EStatus.WAITING));

        String acceptanceNumber = ACCEPTANCE_PREFIX + SEPARATOR + currentDate.getDayOfMonth()
                + SEPARATOR + currentDate.getMonthValue()
                + SEPARATOR + currentDate.getYear()
                + SEPARATOR + acceptance.getId();

        acceptance.setNumber(acceptanceNumber);
        acceptanceRepository.save(acceptance);
    }

    @Transactional
    public void markPurchaseAsInProgress(List<Long> ids) {
        for(Long id: ids){
            Purchase purchase = purchaseRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.purchaseNotFound"));
            purchase.setStatus(EStatus.IN_PROGRESS);
            purchase.setUpdateDate(LocalDateTime.now());
            purchaseRepository.save(purchase);
        }
    }

    @Transactional
    public void markPurchaseAsDone(List<Long> ids) {
        for(Long id: ids){
            Purchase purchase = purchaseRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.purchaseNotFound"));
            purchase.setStatus(EStatus.DONE);
            LocalDateTime currentDate = LocalDateTime.now();
            purchase.setUpdateDate(currentDate);
            purchase.setExecutionDate(currentDate);
            purchaseRepository.save(purchase);
        }
    }
}
