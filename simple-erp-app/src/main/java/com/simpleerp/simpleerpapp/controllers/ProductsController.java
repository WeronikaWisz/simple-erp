package com.simpleerp.simpleerpapp.controllers;

import com.simpleerp.simpleerpapp.dtos.auth.AddUserRequest;
import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.dtos.manageusers.ProfileData;
import com.simpleerp.simpleerpapp.dtos.manageusers.UpdateUserData;
import com.simpleerp.simpleerpapp.dtos.manageusers.UsersResponse;
import com.simpleerp.simpleerpapp.dtos.products.AddProductRequest;
import com.simpleerp.simpleerpapp.dtos.products.ProductCode;
import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.services.ProductsService;
import com.simpleerp.simpleerpapp.services.UsersService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
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

    private ProductCode mapProductToProductCode(Product product){
        return modelMapper.map(product, ProductCode.class);
    }
}
