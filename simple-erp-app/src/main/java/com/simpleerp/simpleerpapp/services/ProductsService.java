package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.production.ProductProductionInfo;
import com.simpleerp.simpleerpapp.dtos.production.ProductionProductQuantity;
import com.simpleerp.simpleerpapp.dtos.products.*;
import com.simpleerp.simpleerpapp.dtos.warehouse.ReleaseProductQuantity;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductsService {

    private final ProductRepository productRepository;
    private final ProductSetRepository productSetRepository;
    private final ProductSetProductsRepository productSetProductsRepository;
    private final ProductProductionRepository productProductionRepository;
    private final ProductProductionProductsRepository productProductionProductsRepository;
    private final ProductionStepRepository productionStepRepository;
    private final StockLevelRepository stockLevelRepository;
    private final ContractorRepository contractorRepository;

    @Autowired
    public ProductsService(ProductRepository productRepository, ProductSetRepository productSetRepository,
                           ProductSetProductsRepository productSetProductsRepository,
                           StockLevelRepository stockLevelRepository, ContractorRepository contractorRepository,
                           ProductProductionRepository productProductionRepository,
                           ProductProductionProductsRepository productProductionProductsRepository,
                           ProductionStepRepository productionStepRepository) {
        this.productRepository = productRepository;
        this.productSetRepository = productSetRepository;
        this.productSetProductsRepository = productSetProductsRepository;
        this.stockLevelRepository = stockLevelRepository;
        this.contractorRepository = contractorRepository;
        this.productProductionRepository = productProductionRepository;
        this.productProductionProductsRepository = productProductionProductsRepository;
        this.productionStepRepository = productionStepRepository;
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
        } else if (addProductRequest.getType().equals(EType.PRODUCED)){
            addProducedProduct(addProductRequest);
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

    private void addProducedProduct(AddProductRequest addProductRequest) {
        BigDecimal salePrice = null;
        if(addProductRequest.getSalePrice() != null && !addProductRequest.getSalePrice().isEmpty()) {
            salePrice = new BigDecimal(addProductRequest.getSalePrice());
        }
        Product product = productRepository.save(new Product(addProductRequest.getCode(), addProductRequest.getName(), null,
                salePrice, addProductRequest.getUnit(), addProductRequest.getType(), LocalDateTime.now()));

        ProductProduction productProduction = productProductionRepository.save(new ProductProduction(
                addProductRequest.getCode(), LocalDateTime.now()));

        for(ProductQuantity productQuantity: addProductRequest.getProductSet()){
            Product subProduct = productRepository.findById(productQuantity.getProduct())
                    .orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
            ProductProductionProducts productProductionProducts = new ProductProductionProducts(productProduction,
                    subProduct, new BigDecimal(productQuantity.getQuantity()));
            productProduction.addProduct(subProduct, new BigDecimal(productQuantity.getQuantity()));
            productProductionProductsRepository.save(productProductionProducts);
        }

        for(int i=1; i<=addProductRequest.getProductionSteps().size(); i++){
            productionStepRepository.save(new ProductionStep(i, addProductRequest.getProductionSteps().get(i-1).getDescription(),
                    productProduction, LocalDateTime.now()));
        }

        StockLevel stockLevel = new StockLevel(product, BigDecimal.ZERO, BigDecimal.ZERO,
                0, LocalDateTime.now());
        stockLevelRepository.save(stockLevel);
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
        if(addProductRequest.getContractor() != null){
            Contractor contractor = contractorRepository.findById(addProductRequest.getContractor())
                    .orElseThrow(() -> new ApiNotFoundException("exception.contractorNotFound"));
            product.setContractor(contractor);
        }
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
            if(product.getContractor() != null){
                productListItem.setContractor(product.getContractor().getId());
            }
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

    @Transactional
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
            Optional<List<ProductProductionProducts>> productProductionProductsList = productProductionProductsRepository.findByProduct(product);
            if(productSetProductsList.isPresent() && !productSetProductsList.get().isEmpty()
            || productProductionProductsList.isPresent() && !productProductionProductsList.get().isEmpty()){
                throw new ApiExpectationFailedException("exception.productIsInSet");
            } else {
                if(type.equals(EType.PRODUCED)){
                    ProductProduction productProduction = productProductionRepository.findByProductCode(product.getCode())
                            .orElseThrow(() -> new ApiNotFoundException("exception.productProductionNotFound"));
                    List<ProductProductionProducts> productProductionProducts = productProduction.getProductProductionProducts();
                    productProductionProductsRepository.deleteAll(productProductionProducts);
                    List<ProductionStep> productionSteps = productionStepRepository.findByProductProduction(productProduction)
                            .orElse(Collections.emptyList());
                    productionStepRepository.deleteAll(productionSteps);
                    productProductionRepository.delete(productProduction);
                }

                StockLevel stockLevel = product.getStockLevel();
                stockLevelRepository.delete(stockLevel);
                productRepository.delete(product);
            }
        }
    }

    public UpdateProductRequest getProduct(Long id, EType type) {
        UpdateProductRequest updateProductRequest;
        if(type.equals(EType.SET)){
            ProductSet productSet = productSetRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
            updateProductRequest = new UpdateProductRequest(productSet.getId(), EType.SET, productSet.getCode(),
                    productSet.getName(), EUnit.PIECES, "",
                    productSet.getSalePrice() != null ? productSet.getSalePrice().toString() : "");
            List<ProductQuantity> productQuantityList = new ArrayList<>();
            for(ProductSetProducts productSetProductsList : productSet.getProductsSets()){
                ProductQuantity productQuantity = new ProductQuantity(productSetProductsList.getProduct().getId(),
                        productSetProductsList.getQuantity().toString());
                productQuantityList.add(productQuantity);
            }
            updateProductRequest.setProductSet(productQuantityList);
        } else {
            Product product = productRepository.findById(id).
                    orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
            updateProductRequest = new UpdateProductRequest(product.getId(), product.getType(), product.getCode(),
                    product.getName(), product.getUnit(),
                    product.getPurchasePrice() != null ? product.getPurchasePrice().toString() : "",
                    product.getSalePrice() != null ? product.getSalePrice().toString() : "");
            if(product.getContractor() != null){
                updateProductRequest.setContractor(product.getContractor().getId());
            }
            if(product.getType().equals(EType.PRODUCED)){
                List<ProductQuantity> productQuantityList = new ArrayList<>();
                ProductProduction productProduction = productProductionRepository.findByProductCode(
                                product.getCode())
                        .orElseThrow(() -> new ApiNotFoundException("exception.productProductionNotFound"));
                for(ProductProductionProducts productProductionProduct: productProduction.getProductProductionProducts()){
                    ProductQuantity productQuantity = new ProductQuantity(productProductionProduct.getProduct().getId(),
                            productProductionProduct.getQuantity().toString());
                    productQuantityList.add(productQuantity);
                }
                updateProductRequest.setProductSet(productQuantityList);
                List<ProductStepDescription> productStepDescriptionList = new ArrayList<>();
                for(ProductionStep productionStep: productProduction.getProductionSteps()){
                    ProductStepDescription productStepDescription = new ProductStepDescription(productionStep.getDescription());
                    productStepDescriptionList.add(productStepDescription);
                }
                updateProductRequest.setProductionSteps(productStepDescriptionList);
            }
        }
        return updateProductRequest;
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
        if(updateProductRequest.getContractor() != null){
            Contractor contractor = contractorRepository.findById(updateProductRequest.getContractor())
                    .orElseThrow(() -> new ApiNotFoundException("exception.contractorNotFound"));
            product.setContractor(contractor);
        } else {
            product.setContractor(null);
        }
        if(product.getType().equals(EType.PRODUCED)){
            this.updateProducedProduct(updateProductRequest, product);
        }
        product.setUpdateDate(LocalDateTime.now());
        productRepository.save(product);
    }

    private void updateProducedProduct(UpdateProductRequest updateProductRequest, Product product) {
        ProductProduction productProduction = productProductionRepository.findByProductCode(
                        product.getCode())
                .orElseThrow(() -> new ApiNotFoundException("exception.productProductionNotFound"));

        List<ProductProductionProducts> productProductionProducts = productProduction.getProductProductionProducts();
        for(ProductQuantity productQuantity: updateProductRequest.getProductSet()){
            Product subProduct = productRepository.findById(productQuantity.getProduct())
                    .orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
            Optional<ProductProductionProducts> productAlreadyInList = productProductionProducts
                    .stream().filter(currentProduct -> currentProduct.getProduct().getId().equals(subProduct.getId())).findFirst();
            if(productAlreadyInList.isPresent()){
                productAlreadyInList.get().setQuantity(new BigDecimal(productQuantity.getQuantity()));
                productProductionProductsRepository.save(productAlreadyInList.get());
            } else {
                ProductProductionProducts newProductProductionProducts = new ProductProductionProducts(productProduction, product,
                        new BigDecimal(productQuantity.getQuantity()));
                productProductionProductsRepository.save(newProductProductionProducts);
            }
        }

        List<ProductProductionProducts> productProductionProductsToDelete = new ArrayList<>();
        for(ProductProductionProducts productProductionProduct: productProductionProducts){
            if(!updateProductRequest.getProductSet().stream().map(ProductQuantity::getProduct)
                    .collect(Collectors.toList()).contains(productProductionProduct.getProduct().getId())){
                productProductionProductsToDelete.add(productProductionProduct);
            }
        }
        productProductionProductsRepository.deleteAll(productProductionProductsToDelete);

        List<ProductionStep> productionSteps = productionStepRepository.findByProductProduction(productProduction)
                .orElse(Collections.emptyList());

        for(int i=1; i<=updateProductRequest.getProductionSteps().size(); i++){
            Optional<ProductionStep> productionStep = productionStepRepository.findByProductProductionAndNumber(productProduction, i);
            if(productionStep.isPresent()){
                productionStep.get().setDescription(updateProductRequest.getProductionSteps().get(i-1).getDescription());
                productionStep.get().setUpdateDate(LocalDateTime.now());
                productionStepRepository.save(productionStep.get());
            } else {
                ProductionStep newProductionStep = new ProductionStep(i, updateProductRequest.getProductionSteps().get(i-1).getDescription(),
                        productProduction, LocalDateTime.now());
                productionStepRepository.save(newProductionStep);
            }
        }

        if(productionSteps.size() > updateProductRequest.getProductionSteps().size()){
            Integer numbersToDelete = productionSteps.size() - (productionSteps.size() - updateProductRequest.getProductionSteps().size());
            Optional<List<ProductionStep>> productionStepsToDelete = productionStepRepository.findByProductProductionAndNumberAfter(
                    productProduction, numbersToDelete);
            if(productionStepsToDelete.isPresent() && !productionStepsToDelete.get().isEmpty()) {
                productionStepRepository.deleteAll(productionStepsToDelete.get());
            }
        }

    }

    public void addContractor(AddContractorRequest addContractorRequest) {
        Optional<Contractor> contractor = contractorRepository.findByEmail(addContractorRequest.getEmail());

        if(contractor.isPresent()){
            throw new ApiExpectationFailedException("exception.emailUsed");
        }

        String phoneNumber = addContractorRequest.getPhone();
        if(!Objects.equals(phoneNumber, "")) {
            phoneNumber = phoneNumber.replaceAll("\\s+", "");
            if (!phoneNumber.startsWith("+48")) {
                phoneNumber = "+48" + phoneNumber;
            }
        }

        contractorRepository.save(new Contractor(addContractorRequest.getName(),
                addContractorRequest.getCountry().toUpperCase(Locale.ROOT),
                addContractorRequest.getNip(), addContractorRequest.getBankAccount(),
                addContractorRequest.getAccountNumber(), addContractorRequest.getUrl(),
                addContractorRequest.getEmail(), phoneNumber, addContractorRequest.getPostalCode(),
                addContractorRequest.getPost(), addContractorRequest.getCity(), addContractorRequest.getStreet(),
                addContractorRequest.getBuildingNumber(), addContractorRequest.getDoorNumber(), LocalDateTime.now()));

    }

    public ContractorsResponse loadContractors(int page, int size) {
        ContractorsResponse contractorsResponse = new ContractorsResponse();
        List<Contractor> contractorList = contractorRepository.findAll();
        int total = contractorList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            contractorsResponse.setContractorsList(contractorListToContractorListItem(contractorList).stream()
                    .sorted(Comparator.comparing(ContractorListItem::getName))
                    .collect(Collectors.toList()).subList(start, end));
        }
        contractorsResponse.setTotalContractorsLength(total);
        return contractorsResponse;
    }

    private List<ContractorListItem> contractorListToContractorListItem(List<Contractor> contractorList){
        List<ContractorListItem> contractorListItems = new ArrayList<>();
        for(Contractor contractor: contractorList){
            ContractorListItem contractorListItem = new ContractorListItem(contractor.getId(), contractor.getName(),
                    contractor.getCountry(), contractor.getNip(), contractor.getUrl(), contractor.getEmail(),
                    contractor.getPhone());
            contractorListItems.add(contractorListItem);
        }
        return contractorListItems;
    }

    public UpdateContractorRequest getContractor(Long id) {
        Contractor contractor = contractorRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.contractorNotFound"));
        return new UpdateContractorRequest(contractor.getId(), contractor.getName(),
                contractor.getCountry(), contractor.getNip(), contractor.getBankAccount(), contractor.getAccountNumber(),
                contractor.getUrl(), contractor.getEmail(), contractor.getPhone(), contractor.getPostalCode(), contractor.getPost(),
                contractor.getCity(), contractor.getStreet(), contractor.getBuildingNumber(), contractor.getDoorNumber());
    }

    @Transactional
    public void deleteContractor(Long id) {
        Contractor contractor = contractorRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.contractorNotFound"));
        List<Product> productList = productRepository.findByContractor(contractor)
                        .orElse(Collections.emptyList());
        if(!productList.isEmpty()){
            productList.forEach(product -> {
                product.setContractor(null);
                productRepository.save(product);
            });
        }
        contractorRepository.delete(contractor);
    }

    @Transactional
    public void updateContractor(UpdateContractorRequest updateContractorRequest) {
        Contractor contractor = contractorRepository.findById(updateContractorRequest.getId())
                .orElseThrow(() -> new ApiNotFoundException("exception.contractorNotFound"));

        String phoneNumber = updateContractorRequest.getPhone();
        if(!Objects.equals(phoneNumber, "")) {
            phoneNumber = phoneNumber.replaceAll("\\s+", "");
            if (!phoneNumber.startsWith("+48")) {
                phoneNumber = "+48" + phoneNumber;
            }
        }

        boolean hasChanged = false;
        if(!Objects.equals(contractor.getName(), updateContractorRequest.getName())){
            contractor.setName(updateContractorRequest.getName());
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getCountry(), updateContractorRequest.getCountry().toUpperCase(Locale.ROOT))){
            contractor.setCountry(updateContractorRequest.getCountry().toUpperCase(Locale.ROOT));
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getNip(), updateContractorRequest.getNip())){
            contractor.setNip(updateContractorRequest.getNip());
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getEmail(), updateContractorRequest.getEmail())){
            if(contractorRepository.findByEmail(updateContractorRequest.getEmail()).isPresent()){
                throw new ApiExpectationFailedException("exception.emailUsed");
            }
            contractor.setEmail(updateContractorRequest.getEmail());
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getPhone(), phoneNumber)){
            contractor.setPhone(phoneNumber);
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getUrl(), updateContractorRequest.getUrl())){
            contractor.setUrl(updateContractorRequest.getUrl());
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getPostalCode(), updateContractorRequest.getPostalCode())){
            contractor.setPostalCode(updateContractorRequest.getPostalCode());
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getPost(), updateContractorRequest.getPost())){
            contractor.setPost(updateContractorRequest.getPost());
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getCity(), updateContractorRequest.getCity())){
            contractor.setCity(updateContractorRequest.getCity());
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getStreet(), updateContractorRequest.getStreet())){
            contractor.setStreet(updateContractorRequest.getStreet());
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getBuildingNumber(), updateContractorRequest.getBuildingNumber())){
            contractor.setBuildingNumber(updateContractorRequest.getBuildingNumber());
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getDoorNumber(), updateContractorRequest.getDoorNumber())){
            contractor.setDoorNumber(updateContractorRequest.getDoorNumber());
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getBankAccount(), updateContractorRequest.getBankAccount())){
            contractor.setBankAccount(updateContractorRequest.getBankAccount());
            hasChanged = true;
        }
        if(!Objects.equals(contractor.getAccountNumber(), updateContractorRequest.getAccountNumber())){
            contractor.setAccountNumber(updateContractorRequest.getAccountNumber());
            hasChanged = true;
        }
        if(hasChanged){
            contractor.setUpdateDate(LocalDateTime.now());
            contractorRepository.save(contractor);
        }
    }

    public List<Contractor> loadContractorsNames() {
        return contractorRepository.findAll();
    }

    public ProductProductionInfo getProductProduction(Long id) {
        Product product = productRepository.findById(id).
                orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
        ProductProductionInfo productProductionInfo = new ProductProductionInfo(product.getCode(),
                product.getName(), product.getUnit());
        List<ProductionProductQuantity> productQuantityList = new ArrayList<>();
        ProductProduction productProduction = productProductionRepository.findByProductCode(
                product.getCode()).orElseThrow(() -> new ApiNotFoundException("exception.productProductionNotFound"));
        for(ProductProductionProducts productProductionProduct: productProduction.getProductProductionProducts()){
            ProductionProductQuantity productionProductQuantity = new ProductionProductQuantity(
                    productProductionProduct.getProduct().getCode(),
                    productProductionProduct.getQuantity().toString());
            productQuantityList.add(productionProductQuantity);
        }
        productProductionInfo.setProductSet(productQuantityList);

        List<ProductStepDescription> productStepDescriptionList = new ArrayList<>();
        for(ProductionStep productionStep: productProduction.getProductionSteps()){
            ProductStepDescription productStepDescription = new ProductStepDescription(productionStep.getDescription());
            productStepDescriptionList.add(productStepDescription);
        }
        productProductionInfo.setProductionSteps(productStepDescriptionList);
        return productProductionInfo;
    }

    public List<ProductCode> loadProductList() {
        List<Product> productList = productRepository.findAll();
        List<ProductCode> productCodeList = new ArrayList<>();
        for (Product product: productList){
            ProductCode productCode = new ProductCode();
            productCode.setId(product.getId());
            productCode.setName(product.getName());
            productCode.setCode(product.getCode());
            productCode.setUnit(product.getUnit());
            productCodeList.add(productCode);
        }
        return productCodeList;
    }

    public ProductSetInfo getProductSet(Long id) {
        ProductSet productSet = productSetRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.productNotFound"));
        ProductSetInfo productSetInfo = new ProductSetInfo(productSet.getCode(), productSet.getName());
        List<ProductionProductQuantity> productQuantityList = new ArrayList<>();
        for(ProductSetProducts productSetProduct: productSet.getProductsSets()){
            ProductionProductQuantity productionProductQuantity = new ProductionProductQuantity(
                    productSetProduct.getProduct().getCode(), productSetProduct.getQuantity().toString()
            );
            productQuantityList.add(productionProductQuantity);
        }
        productSetInfo.setProductSet(productQuantityList);
        return productSetInfo;
    }
}
