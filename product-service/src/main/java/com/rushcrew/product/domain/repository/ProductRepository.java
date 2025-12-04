package com.rushcrew.product.domain.repository;

import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.entity.ProductOption;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findByIdAndDeletedAtIsNull(UUID productId);

    void flush();

    Optional<ProductOption> findOptionBySkuId(UUID skuId);
}
