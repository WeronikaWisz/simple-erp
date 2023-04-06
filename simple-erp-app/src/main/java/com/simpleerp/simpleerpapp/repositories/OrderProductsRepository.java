package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.OrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductsRepository extends JpaRepository<OrderProducts, Long> {
}
