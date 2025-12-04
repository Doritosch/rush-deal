package com.rushcrew.product.infrastructure.repostiory;

import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.entity.ProductOption;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndDeletedAtIsNull(UUID productId);


    @Query("""
                    SELECT po
                    FROM ProductOption po
                    WHERE po.id = :skuId
                      AND po.deletedAt IS NULL
        """)
    Optional<ProductOption> findOptionBySkuId(UUID skuId);
}
