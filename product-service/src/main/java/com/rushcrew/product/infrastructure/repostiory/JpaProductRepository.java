package com.rushcrew.product.infrastructure.repostiory;

import com.rushcrew.product.domain.entity.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductRepository extends JpaRepository<Product, UUID> {

}
