package com.rushcrew.product.domain.repository;

import com.rushcrew.product.domain.entity.Product;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findByIdAndDeletedAtIsNull(UUID productId);

    void flush();
}
