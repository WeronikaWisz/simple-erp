package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.trade.AddOrderRequest;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.models.Order;
import com.simpleerp.simpleerpapp.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private ProductRepository testProductRepository;
    @Mock
    private ProductSetRepository testProductSetRepository;
    @Mock
    private OrderRepository testOrderRepository;
    @Mock
    private OrderProductsRepository testOrderProductsRepository;
    @Mock
    private CustomerRepository testCustomerRepository;
    @Mock
    private UserRepository testUserRepository;
    @Mock
    private TaskRepository testTaskRepository;
    @Mock
    private ReleaseRepository testReleaseRepository;
    @Mock
    private PurchaseRepository testPurchaseRepository;
    @Mock
    private AcceptanceRepository testAcceptanceRepository;
    @Mock
    private MessageSource messageSource;

    private TradeService testTradeService;


    @BeforeEach
    void setUp() {
        testTradeService = new TradeService(testProductRepository, testProductSetRepository, testOrderRepository,
                testOrderProductsRepository, testCustomerRepository, testUserRepository, testTaskRepository,
                testReleaseRepository, testPurchaseRepository, testAcceptanceRepository, messageSource);
    }

    @Test
    void testAddOrderNumberExists() {
        String number = "NUMBER";

        AddOrderRequest addOrderRequest = new AddOrderRequest();
        addOrderRequest.setNumber(number);

        Order order = new Order();
        order.setNumber(number);

        Mockito.when(testOrderRepository.findByNumber(number)).thenReturn(java.util.Optional.of(order));

        assertThrows(ApiExpectationFailedException.class, () -> testTradeService.addOrder(addOrderRequest));
    }
}