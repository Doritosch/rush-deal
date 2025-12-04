package com.rushcrew.product.infrastructure.repostiory;

import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaProductRepository;

    @Override
    public Product save(Product product) {
        return jpaProductRepository.save(product);
    }

    @Override
    public Optional<Product> findByIdAndDeletedAtIsNull(UUID productId) {
        return jpaProductRepository.findByIdAndDeletedAtIsNull(productId);
    }

    @Override
    public void flush() {
        jpaProductRepository.flush();
    }
}
