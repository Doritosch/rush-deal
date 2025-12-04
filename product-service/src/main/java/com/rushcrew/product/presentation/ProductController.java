package com.rushcrew.product.presentation;

import com.rushcrew.product.application.ProductFilter;
import com.rushcrew.product.application.command.CreateProductCommand;
import com.rushcrew.product.application.command.UpdateProductCommand;
import com.rushcrew.product.application.result.CreateProductResult;
import com.rushcrew.product.application.result.ProductDetailResult;
import com.rushcrew.product.application.result.ProductResult;
import com.rushcrew.product.application.result.UpdateProductResult;
import com.rushcrew.product.application.service.ProductService;
import com.rushcrew.product.presentation.dto.request.CreateProductRequest;
import com.rushcrew.product.presentation.dto.request.UpdateProductRequest;
import com.rushcrew.product.presentation.dto.response.CreateProductResponse;
import com.rushcrew.product.presentation.dto.response.ProductDetailResponse;
import com.rushcrew.product.presentation.dto.response.ProductResponse;
import com.rushcrew.product.presentation.dto.response.UpdateProductResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<CreateProductResponse> createProduct(
        @Valid @RequestBody CreateProductRequest request
    ) {
        CreateProductCommand command = CreateProductCommand.from(request);
        CreateProductResult result = productService.createProduct(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateProductResponse.from(result));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<UpdateProductResponse> updateProduct(
        @PathVariable UUID productId,
        @Valid @RequestBody UpdateProductRequest request
    ) {
        UpdateProductCommand command = UpdateProductCommand.from(request);
        UpdateProductResult result = productService.updateProduct(productId, command);
        return ResponseEntity.ok(UpdateProductResponse.from(result));
    }

    @PostMapping("/{productId}/disable")
    public ResponseEntity<Void> disableProduct(
        @PathVariable UUID productId
    ) {
        productService.disableProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/enable")
    public ResponseEntity<Void> enableProduct(
        @PathVariable UUID productId
    ) {
        productService.enableProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable UUID productId
    ) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
        @RequestParam(required = false) List<String> category,
        @RequestParam(required = false) Long minPrice,
        @RequestParam(required = false) Long maxPrice,
        @SortDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        ProductFilter productFilter = ProductFilter.of(category, minPrice, maxPrice);
        Page<ProductResult> resultList = productService.getProducts(productFilter, pageable);
        Page<ProductResponse> response = resultList.map(ProductResponse::from);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(
        @PathVariable UUID productId
    ) {
        ProductDetailResult result = productService.getProductDetail(productId);
        ProductDetailResponse response = ProductDetailResponse.from(result);
        return ResponseEntity.ok(response);
    }
}
