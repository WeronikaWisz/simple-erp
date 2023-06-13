package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.products.AddProductRequest;
import com.simpleerp.simpleerpapp.dtos.trade.AddOrderRequest;
import com.simpleerp.simpleerpapp.dtos.trade.OrderProductList;
import com.simpleerp.simpleerpapp.enums.EDirection;
import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.enums.EUnit;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.models.*;
import com.simpleerp.simpleerpapp.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private StockLevelRepository testStockLevelRepository;
    @Mock
    private PurchaseRepository testPurchaseRepository;
    @Mock
    private UserRepository testUserRepository;
    @Mock
    private TaskRepository testTaskRepository;
    @Mock
    private ReleaseRepository testReleaseRepository;
    @Mock
    private ProductRepository testProductRepository;
    @Mock
    private AcceptanceRepository testAcceptanceRepository;
    @Mock
    private ProductionRepository testProductionRepository;
    @Mock
    private ProductProductionRepository testProductProductionRepository;
    @Mock
    private ProductForecastingRepository testProductForecastingRepository;
    @Mock
    private ProductSetProductsRepository testProductSetProductsRepository;
    @Mock
    private ProductProductionProductsRepository testProductProductionProductsRepository;
    @Mock
    private MessageSource messageSource;

    private WarehouseService testWarehouseService;

    @BeforeEach
    void setUp() {
        testWarehouseService = new WarehouseService(testStockLevelRepository, testPurchaseRepository, testUserRepository,
                testTaskRepository, messageSource, testReleaseRepository, testProductRepository,
                testAcceptanceRepository, testProductionRepository, testProductProductionRepository,
                testProductForecastingRepository, testProductSetProductsRepository, testProductProductionProductsRepository);
    }

    @Test
    void testMarkReleaseAsDone() {
        BigDecimal productQuantity = new BigDecimal("2");
        BigDecimal stockQuantity = new BigDecimal("5");

        Long id = 1L;
        List<Long> ids = new ArrayList<>();
        ids.add(id);

        String code = "CODE";
        Product product = new Product();
        product.setId(id);
        product.setType(EType.BOUGHT);
        product.setIsDeleted(false);
        product.setCode(code);

        String number = "NUMBER";
        Order order = new Order();
        order.setNumber(number);

        List<OrderProducts> orderProductsList = new ArrayList<>();
        OrderProducts orderProducts = new OrderProducts();
        orderProducts.setProduct(product);
        orderProducts.setOrder(order);
        orderProducts.setQuantity(productQuantity);
        orderProductsList.add(orderProducts);

        order.setOrderProductsSet(orderProductsList);

        Release release = new Release();
        release.setStatus(EStatus.IN_PROGRESS);
        release.setDirection(EDirection.EXTERNAL);
        release.setOrder(order);

        StockLevel stockLevel = new StockLevel();
        stockLevel.setProduct(product);
        stockLevel.setQuantity(stockQuantity);

        Mockito.when(testReleaseRepository.findById(id)).thenReturn(Optional.of(release));
        Mockito.when(testStockLevelRepository.findByProduct(product)).thenReturn(Optional.of(stockLevel));

        testWarehouseService.markReleaseAsDone(ids);

        assertEquals(EStatus.DONE, release.getStatus());
        assertEquals(new BigDecimal("3"), stockLevel.getQuantity());
    }

    @Test
    void testMarkReleaseAsDoneNotEnoughStockLevel() {
        BigDecimal productQuantity = new BigDecimal("2");
        BigDecimal stockQuantity = new BigDecimal("1");

        Long id = 1L;
        List<Long> ids = new ArrayList<>();
        ids.add(id);

        String code = "CODE";
        Product product = new Product();
        product.setId(id);
        product.setType(EType.BOUGHT);
        product.setIsDeleted(false);
        product.setCode(code);

        String number = "NUMBER";
        Order order = new Order();
        order.setNumber(number);

        List<OrderProducts> orderProductsList = new ArrayList<>();
        OrderProducts orderProducts = new OrderProducts();
        orderProducts.setProduct(product);
        orderProducts.setOrder(order);
        orderProducts.setQuantity(productQuantity);
        orderProductsList.add(orderProducts);

        order.setOrderProductsSet(orderProductsList);

        Release release = new Release();
        release.setStatus(EStatus.IN_PROGRESS);
        release.setDirection(EDirection.EXTERNAL);
        release.setOrder(order);

        StockLevel stockLevel = new StockLevel();
        stockLevel.setProduct(product);
        stockLevel.setQuantity(stockQuantity);

        Mockito.when(testReleaseRepository.findById(id)).thenReturn(Optional.of(release));
        Mockito.when(testStockLevelRepository.findByProduct(product)).thenReturn(Optional.of(stockLevel));

        assertThrows(ApiExpectationFailedException.class, () -> testWarehouseService.markReleaseAsDone(ids));
    }

    @Test
    void testMarkAcceptanceAsDone() {
        BigDecimal productQuantity = new BigDecimal("2");
        BigDecimal stockQuantity = new BigDecimal("5");

        Long id = 1L;
        List<Long> ids = new ArrayList<>();
        ids.add(id);

        String code = "CODE";
        Product product = new Product();
        product.setId(id);
        product.setType(EType.BOUGHT);
        product.setIsDeleted(false);
        product.setCode(code);

        Purchase purchase = new Purchase();
        purchase.setProduct(product);
        purchase.setQuantity(productQuantity);

        Acceptance acceptance = new Acceptance();
        acceptance.setStatus(EStatus.IN_PROGRESS);
        acceptance.setDirection(EDirection.EXTERNAL);
        acceptance.setPurchase(purchase);

        StockLevel stockLevel = new StockLevel();
        stockLevel.setProduct(product);
        stockLevel.setQuantity(stockQuantity);

        Mockito.when(testAcceptanceRepository.findById(id)).thenReturn(Optional.of(acceptance));
        Mockito.when(testStockLevelRepository.findByProduct(product)).thenReturn(Optional.of(stockLevel));

        testWarehouseService.markAcceptanceAsDone(ids);

        assertEquals(EStatus.DONE, acceptance.getStatus());
        assertEquals(new BigDecimal("7"), stockLevel.getQuantity());
    }
}