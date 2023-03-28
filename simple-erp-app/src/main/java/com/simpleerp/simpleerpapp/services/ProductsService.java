package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.products.AddProductRequest;
import com.simpleerp.simpleerpapp.dtos.products.ProductQuantity;
import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.models.ProductSet;
import com.simpleerp.simpleerpapp.models.ProductSetProducts;
import com.simpleerp.simpleerpapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductsService {

    private final ProductRepository productRepository;
    private final ProductSetRepository productSetRepository;
    private final ProductSetProductsRepository productSetProductsRepository;

    @Autowired
    public ProductsService(ProductRepository productRepository, ProductSetRepository productSetRepository,
                           ProductSetProductsRepository productSetProductsRepository) {
        this.productRepository = productRepository;
        this.productSetRepository = productSetRepository;
        this.productSetProductsRepository = productSetProductsRepository;
    }

    @Transactional
    public void addProduct(AddProductRequest addProductRequest) {
        Optional<Product> product = productRepository.findByCode(addProductRequest.getCode());
        Optional<ProductSet> productSet = productSetRepository.findByCode(addProductRequest.getCode());
        if(product.isPresent() || productSet.isPresent()){
            throw new ApiExpectationFailedException("exception.productCodeAlreadyUsed");
        }
        if(addProductRequest.getType().equals(EType.SET)){
            addProductSet(addProductRequest);
        } else {
            addSingleProduct(addProductRequest);
        }
    }

    private void addProductSet(AddProductRequest addProductRequest) {
        ProductSet productSet = productSetRepository.save(new ProductSet(addProductRequest.getCode(), addProductRequest.getName(),
                new BigDecimal(addProductRequest.getSalePrice())));

        for(ProductQuantity productQuantity: addProductRequest.getProductSet()){
            Product product = productRepository.findById(productQuantity.getProduct())
                    .orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
            ProductSetProducts productSetProducts = new ProductSetProducts(productSet, product,
                    new BigDecimal(productQuantity.getQuantity()));
            productSet.addProduct(product, new BigDecimal(productQuantity.getQuantity()));
            productSetProductsRepository.save(productSetProducts);
        }
    }

    private void addSingleProduct(AddProductRequest addProductRequest) {
        Product product = new Product(addProductRequest.getCode(), addProductRequest.getName(),
                new BigDecimal(addProductRequest.getPurchasePrice()), new BigDecimal(addProductRequest.getSalePrice()),
                addProductRequest.getUnit(), addProductRequest.getType());
        productRepository.save(product);
    }

    public List<Product> loadProductForSetList() {
        return productRepository.findAll();
    }
}
