package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.products.*;
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
    private final StockLevelRepository stockLevelRepository;

    @Autowired
    public ProductsService(ProductRepository productRepository, ProductSetRepository productSetRepository,
                           ProductSetProductsRepository productSetProductsRepository,
                           StockLevelRepository stockLevelRepository) {
        this.productRepository = productRepository;
        this.productSetRepository = productSetRepository;
        this.productSetProductsRepository = productSetProductsRepository;
        this.stockLevelRepository = stockLevelRepository;
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
        BigDecimal purchasePrice = null;
        BigDecimal salePrice = null;
        if(addProductRequest.getPurchasePrice() != null && !addProductRequest.getPurchasePrice().isEmpty()) {
            purchasePrice = new BigDecimal(addProductRequest.getPurchasePrice());
        }
        if(addProductRequest.getSalePrice() != null && !addProductRequest.getSalePrice().isEmpty()) {
            salePrice = new BigDecimal(addProductRequest.getSalePrice());
        }
        Product product = new Product(addProductRequest.getCode(), addProductRequest.getName(), purchasePrice,
                salePrice, addProductRequest.getUnit(), addProductRequest.getType(), LocalDateTime.now());
        productRepository.save(product);

        StockLevel stockLevel = new StockLevel(product, BigDecimal.ZERO, BigDecimal.ZERO,
                0, LocalDateTime.now());
        stockLevelRepository.save(stockLevel);
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
                    product.getName(), product.getUnit(), product.getPurchasePrice() != null ? product.getPurchasePrice().toString() : "",
                    product.getSalePrice() != null ? product.getSalePrice().toString() : "");
            productListItems.add(productListItem);
        }
        for(ProductSet productSet: productSetList){
            ProductListItem productListItem = new ProductListItem(productSet.getId(), EType.SET, productSet.getCode(),
                    productSet.getName(), EUnit.PIECES, "",
                    productSet.getSalePrice() != null ? productSet.getSalePrice().toString() : "");
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
                StockLevel stockLevel = product.getStockLevel();
                stockLevelRepository.delete(stockLevel);
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
                    productSet.getSalePrice() != null ? productSet.getSalePrice().toString() : "");
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
                    product.getName(), product.getUnit(),
                    product.getPurchasePrice() != null ? product.getPurchasePrice().toString() : "",
                    product.getSalePrice() != null ? product.getSalePrice().toString() : "");
        }
        return productListItem;
    }

    @Transactional
    public void updateProduct(UpdateProductRequest updateProductRequest) {
        if(updateProductRequest.getType().equals(EType.SET)){
            updateProductSet(updateProductRequest);
        } else {
            updateSingleProduct(updateProductRequest);
        }
    }

    private void updateProductSet(UpdateProductRequest updateProductRequest) {
        ProductSet productSet = productSetRepository.findById(updateProductRequest.getId())
                .orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
        List<ProductSetProducts> productSetProducts = productSet.getProductsSets();
        productSet.setCode(updateProductRequest.getCode());
        productSet.setName(updateProductRequest.getName());
        productSet.setSalePrice(new BigDecimal(updateProductRequest.getSalePrice()));
        productSet.setUpdateDate(LocalDateTime.now());

        for(ProductQuantity productQuantity: updateProductRequest.getProductSet()){
            Product product = productRepository.findById(productQuantity.getProduct())
                    .orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
            Optional<ProductSetProducts> productAlreadyInList = productSetProducts.stream()
                    .filter(currentProduct -> currentProduct.getProduct().getId().equals(product.getId())).findFirst();
            if(productAlreadyInList.isPresent()){
                productAlreadyInList.get().setQuantity(new BigDecimal(productQuantity.getQuantity()));
                productSetProductsRepository.save(productAlreadyInList.get());
            } else {
                ProductSetProducts newProductSetProducts = new ProductSetProducts(productSet, product,
                    new BigDecimal(productQuantity.getQuantity()));
                productSetProductsRepository.save(newProductSetProducts);
            }
        }

        List<ProductSetProducts> productSetProductsToDelete = new ArrayList<>();
        for(ProductSetProducts productSetProduct: productSetProducts){
            if(!updateProductRequest.getProductSet().stream().map(ProductQuantity::getProduct)
                    .collect(Collectors.toList()).contains(productSetProduct.getProduct().getId())){
                productSetProductsToDelete.add(productSetProduct);
            }
        }
        productSetProductsRepository.deleteAll(productSetProductsToDelete);
    }

    private void updateSingleProduct(UpdateProductRequest updateProductRequest){
        Product product = productRepository.findById(updateProductRequest.getId()).
                orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
        product.setCode(updateProductRequest.getCode());
        product.setName(updateProductRequest.getName());
        product.setUnit(updateProductRequest.getUnit());
        if(updateProductRequest.getPurchasePrice() != null && !updateProductRequest.getPurchasePrice().isEmpty()) {
            product.setPurchasePrice(new BigDecimal(updateProductRequest.getPurchasePrice()));
        } else {
            product.setPurchasePrice(null);
        }
        if(updateProductRequest.getSalePrice() != null && !updateProductRequest.getSalePrice().isEmpty()) {
            product.setSalePrice(new BigDecimal(updateProductRequest.getSalePrice()));
        } else {
            product.setSalePrice(null);
        }
        product.setUpdateDate(LocalDateTime.now());
        productRepository.save(product);
    }
}
