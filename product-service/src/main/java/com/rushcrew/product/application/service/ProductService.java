package com.rushcrew.product.application.service;

import com.rushcrew.product.application.ProductFilter;
import com.rushcrew.product.application.command.CreateProductCommand;
import com.rushcrew.product.application.command.UpdateProductCommand;
import com.rushcrew.product.application.result.CreateProductResult;
import com.rushcrew.product.application.result.ProductDetailResult;
import com.rushcrew.product.application.result.ProductResult;
import com.rushcrew.product.application.result.UpdateProductResult;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    CreateProductResult createProduct(Long userId, String role, CreateProductCommand command);

    UpdateProductResult updateProduct(Long userId, String role, UUID productId,
        UpdateProductCommand command);

    void disableProduct(Long userId, String role, UUID productId);

    void enableProduct(Long userId, String role, UUID productId);

    void deleteProduct(Long userId, String role, UUID productId);

    Page<ProductResult> getProducts(ProductFilter productFilter, Pageable pageable);

    ProductDetailResult getProductDetail(UUID productId);
}
