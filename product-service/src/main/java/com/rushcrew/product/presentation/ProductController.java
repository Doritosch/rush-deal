package com.rushcrew.product.presentation;

import com.rushcrew.product.application.command.CreateProductCommand;
import com.rushcrew.product.application.result.CreateProductResult;
import com.rushcrew.product.application.service.ProductService;
import com.rushcrew.product.presentation.dto.request.CreateProductRequest;
import com.rushcrew.product.presentation.dto.response.CreateProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
