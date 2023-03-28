package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.ProductSetProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSetProductsRepository extends JpaRepository<ProductSetProducts, Long> {
}
