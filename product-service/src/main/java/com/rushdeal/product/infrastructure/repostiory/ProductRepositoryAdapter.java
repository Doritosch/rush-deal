package com.rushdeal.product.infrastructure.repostiory;

import com.rushdeal.product.domain.entity.Product;
import com.rushdeal.product.domain.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;

    @Override
    public Product save(Product product) {
        return jpaProductRepository.save(product);
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return jpaProductRepository.findById(productId);
    }
}
