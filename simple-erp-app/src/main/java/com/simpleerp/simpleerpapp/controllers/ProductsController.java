package com.simpleerp.simpleerpapp.controllers;

import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.dtos.production.ProductProductionInfo;
import com.simpleerp.simpleerpapp.dtos.products.*;
import com.simpleerp.simpleerpapp.dtos.trade.ContractorName;
import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.models.Contractor;
import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.services.ProductsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@CrossOrigin("http://localhost:4200")
public class ProductsController {

    private final ProductsService productsService;
    private MessageSource messageSource;
    private ModelMapper modelMapper;

    @Autowired
    public ProductsController(ProductsService productsService, MessageSource messageSource, ModelMapper modelMapper) {
        this.productsService = productsService;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> loadProducts(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        ProductsResponse productsResponse = productsService.loadProducts(page, size);
        return ResponseEntity.ok(productsResponse);
    }

    @GetMapping("/set-products")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> loadProductForSetList() {
        List<ProductCode> productCodeList = productsService.loadProductForSetList()
                .stream().map(this::mapProductToProductCode).collect(Collectors.toList());
        return ResponseEntity.ok(productCodeList);
    }

    @PostMapping("/product")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addProduct(@RequestBody AddProductRequest addProductRequest) {
        productsService.addProduct(addProductRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.productAdded", null, LocaleContextHolder.getLocale())));
    }

    @DeleteMapping("/product/{type}/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable("type") EType type, @PathVariable("id") Long id) {
        productsService.deleteProduct(id, type);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.productDeleted", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/product/{type}/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getProduct(@PathVariable("type") EType type, @PathVariable("id") Long id) {
        UpdateProductRequest product = productsService.getProduct(id, type);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/product")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateProduct(@RequestBody UpdateProductRequest updateProductRequest) {
        productsService.updateProduct(updateProductRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.productUpdate", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("/contractor")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addContractor(@RequestBody AddContractorRequest addContractorRequest) {
        productsService.addContractor(addContractorRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.contractorAdded", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/contractors")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> loadContractors(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        ContractorsResponse contractorsResponse = productsService.loadContractors(page, size);
        return ResponseEntity.ok(contractorsResponse);
    }

    @DeleteMapping("/contractor/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteContractor(@PathVariable("id") Long id) {
        productsService.deleteContractor(id);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.productDeleted", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/contractor/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getContractor(@PathVariable("id") Long id) {
        UpdateContractorRequest contractor = productsService.getContractor(id);
        return ResponseEntity.ok(contractor);
    }

    @PutMapping("/contractor")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateContractor(@RequestBody UpdateContractorRequest updateContractorRequest) {
        productsService.updateContractor(updateContractorRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.contractorUpdate", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/contractors-names")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> loadContractorsNames() {
        List<ContractorName> contractorsNames = productsService.loadContractorsNames()
                .stream().map(this::mapContractorToContractorName).collect(Collectors.toList());
        return ResponseEntity.ok(contractorsNames);
    }

    @GetMapping("/product-info/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getProductProduction(@PathVariable("id") Long id) {
        ProductProductionInfo productProductionInfo = productsService.getProductProduction(id);
        return ResponseEntity.ok(productProductionInfo);
    }

    @GetMapping("/only-products")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> loadProductList() {
        List<ProductCode> productCodeList = productsService.loadProductList();
        return ResponseEntity.ok(productCodeList);
    }

    private ContractorName mapContractorToContractorName(Contractor contractor){
        return modelMapper.map(contractor, ContractorName.class);
    }

    private ProductCode mapProductToProductCode(Product product){
        return modelMapper.map(product, ProductCode.class);
    }
}
