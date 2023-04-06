package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.products.ProductCode;
import com.simpleerp.simpleerpapp.dtos.trade.AddOrderRequest;
import com.simpleerp.simpleerpapp.dtos.trade.OrderProductQuantity;
import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.enums.EUnit;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.models.*;
import com.simpleerp.simpleerpapp.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TradeService {

    private final ProductRepository productRepository;
    private final ProductSetRepository productSetRepository;
    private final OrderRepository orderRepository;
    private final OrderProductsRepository orderProductsRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public TradeService(ProductRepository productRepository, ProductSetRepository productSetRepository,
                        OrderRepository orderRepository, OrderProductsRepository orderProductsRepository,
                        CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.productSetRepository = productSetRepository;
        this.orderRepository = orderRepository;
        this.orderProductsRepository = orderProductsRepository;
        this.customerRepository = customerRepository;
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

        Customer customer = findOrCreateCustomer(addOrderRequest);

        Order order = orderRepository.save(new Order(addOrderRequest.getNumber(), addOrderRequest.getOrderDate(),
                EStatus.WAITING, customer, LocalDateTime.now()));

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
        if(!addOrderRequest.getPrice().isEmpty()){
            totalPrice = new BigDecimal(addOrderRequest.getPrice());
            order.setPrice(new BigDecimal(addOrderRequest.getPrice()));
        } else {
            totalPrice = calculatedPrice;
            order.setPrice(calculatedPrice);
        }
        if(!addOrderRequest.getDiscount().isEmpty()){
            totalPrice = totalPrice.subtract(totalPrice.multiply(
                    (new BigDecimal(addOrderRequest.getDiscount()))
                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));
            order.setDiscount(new BigDecimal(addOrderRequest.getDiscount()));
        }
        if(!addOrderRequest.getDelivery().isEmpty()){
            totalPrice = totalPrice.add(new BigDecimal(addOrderRequest.getDelivery()));
            order.setDelivery(new BigDecimal(addOrderRequest.getDelivery()));
        }
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
    }

    private Customer findOrCreateCustomer(AddOrderRequest addOrderRequest){
        Optional<Customer> customer = customerRepository.findByNameAndSurnameAndEmail(addOrderRequest.getName(),
                addOrderRequest.getSurname(), addOrderRequest.getEmail());

        String phoneNumber = addOrderRequest.getPhone();
        if(!Objects.equals(phoneNumber, "")) {
            phoneNumber = phoneNumber.replaceAll("\\s+", "");
            if (!phoneNumber.startsWith("+48")) {
                phoneNumber = "+48" + phoneNumber;
            }
        }

        if(customer.isPresent()){
            boolean hasChanged = false;
            if(!Objects.equals(customer.get().getPhone(), phoneNumber)){
                customer.get().setPhone(phoneNumber);
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getPostalCode(), addOrderRequest.getPostalCode())){
                customer.get().setPostalCode(addOrderRequest.getPostalCode());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getPost(), addOrderRequest.getPost())){
                customer.get().setPost(addOrderRequest.getPost());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getCity(), addOrderRequest.getCity())){
                customer.get().setCity(addOrderRequest.getCity());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getStreet(), addOrderRequest.getStreet())){
                customer.get().setStreet(addOrderRequest.getStreet());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getBuildingNumber(), addOrderRequest.getBuildingNumber())){
                customer.get().setBuildingNumber(addOrderRequest.getBuildingNumber());
                hasChanged = true;
            }
            if(!Objects.equals(customer.get().getDoorNumber(), addOrderRequest.getDoorNumber())){
                customer.get().setDoorNumber(addOrderRequest.getDoorNumber());
                hasChanged = true;
            }
            if(hasChanged){
                customer.get().setUpdateDate(LocalDateTime.now());
            }
            return customer.get();
        } else {
            return customerRepository.save(new Customer(addOrderRequest.getName(), addOrderRequest.getSurname(),
                    addOrderRequest.getEmail(), phoneNumber, addOrderRequest.getPostalCode(),
                    addOrderRequest.getPost(), addOrderRequest.getCity(), addOrderRequest.getStreet(),
                    addOrderRequest.getBuildingNumber(), addOrderRequest.getDoorNumber(), LocalDateTime.now()));
        }
    }
}
