package com.rushdeal.product.domain.repository;

import com.rushdeal.product.domain.entity.Product;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID productId);
}
