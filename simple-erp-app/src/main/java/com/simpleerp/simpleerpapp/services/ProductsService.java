package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.manageusers.UserListItem;
import com.simpleerp.simpleerpapp.dtos.manageusers.UsersResponse;
import com.simpleerp.simpleerpapp.dtos.products.AddProductRequest;
import com.simpleerp.simpleerpapp.dtos.products.ProductListItem;
import com.simpleerp.simpleerpapp.dtos.products.ProductQuantity;
import com.simpleerp.simpleerpapp.dtos.products.ProductsResponse;
import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.enums.EUnit;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.*;
import com.simpleerp.simpleerpapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
                new BigDecimal(addProductRequest.getSalePrice()), LocalDateTime.now()));

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
                addProductRequest.getUnit(), addProductRequest.getType(), LocalDateTime.now());
        productRepository.save(product);
    }

    public List<Product> loadProductForSetList() {
        return productRepository.findAll();
    }

    public ProductsResponse loadProducts(int page, int size) {
        ProductsResponse productsResponse = new ProductsResponse();
        List<Product> productList = productRepository.findAll();
        List<ProductSet> productSetList = productSetRepository.findAll();
        int total = productList.size() + productSetList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            productsResponse.setProductsList(productListToProductListItem(productList, productSetList).stream()
                    .sorted(Comparator.comparing(ProductListItem::getCode))
                    .collect(Collectors.toList()).subList(start, end));
        }
        productsResponse.setTotalProductsLength(total);
        return productsResponse;
    }

    private List<ProductListItem> productListToProductListItem(List<Product> productList, List<ProductSet> productSetList){
        List<ProductListItem> productListItems = new ArrayList<>();
        for(Product product: productList){
            ProductListItem productListItem = new ProductListItem(product.getId(), product.getType(), product.getCode(),
                    product.getName(), product.getUnit(), product.getPurchasePrice().toString(),
                    product.getSalePrice().toString());
            productListItems.add(productListItem);
        }
        for(ProductSet productSet: productSetList){
            ProductListItem productListItem = new ProductListItem(productSet.getId(), EType.SET, productSet.getCode(),
                    productSet.getName(), EUnit.PIECES, "",
                    productSet.getSalePrice().toString());
            productListItems.add(productListItem);
        }
        return productListItems;
    }

    public void deleteProduct(Long id, EType type) {
        if(type.equals(EType.SET)){
            ProductSet productSet = productSetRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
            List<ProductSetProducts> productSetProducts = productSet.getProductsSets();
            productSetProductsRepository.deleteAll(productSetProducts);
            productSetRepository.delete(productSet);
        } else {
            Product product = productRepository.findById(id).
                    orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
            Optional<List<ProductSetProducts>> productSetProductsList = productSetProductsRepository.findByProduct(product);
            if(productSetProductsList.isPresent() && !productSetProductsList.get().isEmpty()){
                throw new ApiExpectationFailedException("exception.productIsInSet");
            } else {
                productRepository.delete(product);
            }
        }
    }

    public ProductListItem getProduct(Long id, EType type) {
        ProductListItem productListItem;
        if(type.equals(EType.SET)){
            ProductSet productSet = productSetRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
            productListItem = new ProductListItem(productSet.getId(), EType.SET, productSet.getCode(),
                    productSet.getName(), EUnit.PIECES, "",
                    productSet.getSalePrice().toString());
            List<ProductQuantity> productQuantityList = new ArrayList<>();
            for(ProductSetProducts productSetProductsList : productSet.getProductsSets()){
                ProductQuantity productQuantity = new ProductQuantity(productSetProductsList.getProduct().getId(),
                        productSetProductsList.getQuantity().toString());
                productQuantityList.add(productQuantity);
            }
            productListItem.setProductSet(productQuantityList);
        } else {
            Product product = productRepository.findById(id).
                    orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
            productListItem = new ProductListItem(product.getId(), product.getType(), product.getCode(),
                    product.getName(), product.getUnit(), product.getPurchasePrice().toString(),
                    product.getSalePrice().toString());
        }
        return productListItem;
    }
}
