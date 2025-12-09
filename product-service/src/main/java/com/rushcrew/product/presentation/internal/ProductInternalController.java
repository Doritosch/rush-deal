package com.rushcrew.product.presentation.internal;

import com.rushcrew.product.application.service.internal.ProductInternalService;
import com.rushcrew.product.presentation.internal.dto.ProductInfoResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/products")
@RequiredArgsConstructor
public class ProductInternalController {

    private final ProductInternalService productService;

    @GetMapping("/{productId}/info")
    public ProductInfoResponse getProductItemIds(
        @PathVariable UUID productId
    ) {
        return productService.getProductItemIds(productId);
    }
}
