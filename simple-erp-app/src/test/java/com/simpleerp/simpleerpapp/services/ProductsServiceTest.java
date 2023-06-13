package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.products.AddProductRequest;
import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.enums.EUnit;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductsServiceTest {

    @Mock
    private ProductRepository testProductRepository;
    @Mock
    private ProductSetRepository testProductSetRepository;
    @Mock
    private ProductSetProductsRepository testProductSetProductsRepository;
    @Mock
    private ProductProductionRepository testProductProductionRepository;
    @Mock
    private ProductProductionProductsRepository testProductProductionProductsRepository;
    @Mock
    private ProductionStepRepository testProductionStepRepository;
    @Mock
    private StockLevelRepository testStockLevelRepository;
    @Mock
    private ContractorRepository testContractorRepository;
    @Mock
    private PurchaseRepository testPurchaseRepository;
    @Mock
    private ProductionRepository testProductionRepository;
    @Mock
    private OrderRepository testOrderRepository;
    @Mock
    private OrderProductsRepository testOrderProductsRepository;
    @Mock
    private ProductForecastingRepository testProductForecastingRepository;
    @Mock
    private ForecastingTrainingEvaluationRepository testForecastingTrainingEvaluationRepository;

    private ProductsService testProductsService;


    @BeforeEach
    void setUp() {
        testProductsService = new ProductsService(testProductRepository, testProductSetRepository,
                testProductSetProductsRepository, testStockLevelRepository, testContractorRepository,
                testProductProductionRepository, testProductProductionProductsRepository, testProductionStepRepository,
                testPurchaseRepository, testProductionRepository, testOrderRepository, testOrderProductsRepository,
                testProductForecastingRepository, testForecastingTrainingEvaluationRepository);
    }

    @Test
    void testAddProduct() {
        String code = "CODE";

        AddProductRequest addProductRequest = new AddProductRequest();
        addProductRequest.setCode(code);
        addProductRequest.setName("name");
        addProductRequest.setType(EType.BOUGHT);
        addProductRequest.setPurchasePrice("20");
        addProductRequest.setPurchaseVat("23");
        addProductRequest.setUnit(EUnit.PIECES);

        Product product = new Product();
        product.setCode(code);

        Mockito.when(testProductRepository.findByCode(code)).thenReturn(Optional.empty());
        Mockito.when(testProductSetRepository.findByCode(code)).thenReturn(Optional.empty());

        testProductsService.addProduct(addProductRequest);

        verify(testProductRepository).save(any(Product.class));
    }

//    @Test
//    void testAddProductSet() {
//        String code = "CODE";
//
//        AddProductRequest addProductRequest = new AddProductRequest();
//        addProductRequest.setCode(code);
//        addProductRequest.setName("name");
//        addProductRequest.setType(EType.SET);
//        addProductRequest.setSalePrice("20");
//        addProductRequest.setSaleVat("23");
//        addProductRequest.setUnit(EUnit.PIECES);
//
//        Long id = 1L;
//        String code2 = "CODE2";
//        Product product = new Product();
//        product.setId(id);
//        product.setType(EType.BOUGHT);
//        product.setIsDeleted(false);
//        product.setCode(code2);
//
//        List<ProductQuantity> productQuantityList = new ArrayList<>();
//        ProductQuantity productQuantity = new ProductQuantity();
//        productQuantity.setProduct(id);
//        productQuantity.setQuantity("2");
//        productQuantityList.add(productQuantity);
//        addProductRequest.setProductSet(productQuantityList);
//
//        Mockito.when(testProductRepository.findByCode(code)).thenReturn(Optional.empty());
//        Mockito.when(testProductSetRepository.findByCode(code)).thenReturn(Optional.empty());
//
//        Mockito.when(testProductRepository.findById(id)).thenReturn(Optional.of(product));
//
//        testProductsService.addProduct(addProductRequest);
//
//        verify(testProductSetRepository).save(any(ProductSet.class));
//    }

    @Test
    void testAddProductCodeExists() {
        String code = "CODE";

        AddProductRequest addProductRequest = new AddProductRequest();
        addProductRequest.setCode(code);
        addProductRequest.setName("name");
        addProductRequest.setType(EType.BOUGHT);
        addProductRequest.setPurchasePrice("20");
        addProductRequest.setPurchaseVat("23");
        addProductRequest.setUnit(EUnit.PIECES);

        Product product = new Product();
        product.setCode(code);

        Mockito.when(testProductRepository.findByCode(code)).thenReturn(java.util.Optional.of(product));
        Mockito.when(testProductSetRepository.findByCode(code)).thenReturn(Optional.empty());

        assertThrows(ApiExpectationFailedException.class, () -> testProductsService.addProduct(addProductRequest));
    }
}