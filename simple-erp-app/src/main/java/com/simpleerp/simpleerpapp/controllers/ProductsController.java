package com.simpleerp.simpleerpapp.controllers;

import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.dtos.auth.UpdateUserRequest;
import com.simpleerp.simpleerpapp.dtos.products.*;
import com.simpleerp.simpleerpapp.enums.EType;
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
    public ResponseEntity<?> addUser(@RequestBody AddProductRequest addProductRequest) {
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
        ProductListItem product = productsService.getProduct(id, type);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/product")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateProduct(@RequestBody UpdateProductRequest updateProductRequest) {
        productsService.updateProduct(updateProductRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.productUpdate", null, LocaleContextHolder.getLocale())));
    }

    private ProductCode mapProductToProductCode(Product product){
        return modelMapper.map(product, ProductCode.class);
    }
}
