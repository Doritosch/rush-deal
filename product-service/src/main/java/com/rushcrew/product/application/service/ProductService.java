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

    CreateProductResult createProduct(CreateProductCommand command);

    UpdateProductResult updateProduct(UUID productId, UpdateProductCommand command);

    void disableProduct(UUID productId);

    void enableProduct(UUID productId);

    void deleteProduct(UUID productId);

    Page<ProductResult> getProducts(ProductFilter productFilter, Pageable pageable);

    ProductDetailResult getProductDetail(UUID productId);
}
