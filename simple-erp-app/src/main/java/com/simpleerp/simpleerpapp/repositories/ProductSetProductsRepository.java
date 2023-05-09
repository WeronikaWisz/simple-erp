package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.models.ProductSetProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSetProductsRepository extends JpaRepository<ProductSetProducts, Long> {

    List<ProductSetProducts> findByProduct(Product product);

}
