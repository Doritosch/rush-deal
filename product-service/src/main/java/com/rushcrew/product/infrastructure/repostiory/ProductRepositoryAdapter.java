package com.rushcrew.product.infrastructure.repostiory;

import com.rushcrew.product.application.ProductFilter;
import com.rushcrew.product.application.result.ProductResult;
import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.entity.ProductOption;
import com.rushcrew.product.domain.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<ProductResult> searchEnabledProducts(ProductFilter productFilter,
        Pageable pageable) {
        return jpaProductRepository.searchEnabledProducts(productFilter, pageable);
    }

    @Override
    public Optional<Product> findProductDetail(UUID productId) {
        return jpaProductRepository.findProductDetail(productId);
    }

    @Override
    public void saveAndFlush(Product product) {
        jpaProductRepository.saveAndFlush(product);
    }

    @Override
    public Optional<ProductOption> findOptionBySkuId(UUID skuId) {
        return jpaProductRepository.findOptionBySkuId(skuId);
    }
}
