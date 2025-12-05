package com.rushcrew.product.domain.repository;

import com.rushcrew.product.application.ProductFilter;
import com.rushcrew.product.application.result.ProductResult;
import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.entity.ProductOption;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findByIdAndDeletedAtIsNull(UUID productId);

    /**
     * 활성화된(비활성화 X, 논리삭제 X) 상품들을 필터링 및 페이지 조회하는 메서드
     */
    Page<ProductResult> searchEnabledProducts(ProductFilter productFilter, Pageable pageable);

    Optional<Product> findProductDetail(UUID productId);

    void saveAndFlush(Product product);

    Optional<ProductOption> findOptionBySkuId(UUID skuId);
}
