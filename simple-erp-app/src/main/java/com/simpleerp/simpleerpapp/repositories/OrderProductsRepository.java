package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.OrderProducts;
import com.simpleerp.simpleerpapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderProductsRepository extends JpaRepository<OrderProducts, Long> {
    List<OrderProducts> findByProduct(Product product);
}
