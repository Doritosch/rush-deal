package com.rushcrew.product.presentation.internal;

import com.rushcrew.product.application.service.internal.ProductInternalService;
import com.rushcrew.product.presentation.internal.dto.ProductInfoResponse;
import com.rushcrew.product.presentation.internal.dto.ProductItemIdsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/products")
@RequiredArgsConstructor
public class ProductInternalController {

    private final ProductInternalService productService;

    @PostMapping("/item-ids")
    public ProductInfoResponse getProductItemIds(
        @RequestBody ProductItemIdsRequest request
    ) {
        return productService.getProductItemIds(request.productId());
    }
}
