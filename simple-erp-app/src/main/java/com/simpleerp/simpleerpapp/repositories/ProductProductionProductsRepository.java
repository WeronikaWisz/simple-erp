package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.models.ProductProductionProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductProductionProductsRepository extends JpaRepository<ProductProductionProducts, Long> {
    Optional<List<ProductProductionProducts>> findByProduct(Product product);
}
