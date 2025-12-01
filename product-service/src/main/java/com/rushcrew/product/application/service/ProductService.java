package com.rushcrew.product.application.service;

import com.rushcrew.product.application.command.CreateProductCommand;
import com.rushcrew.product.application.result.CreateProductResult;
import com.rushcrew.product.application.result.UpdateProductResult;
import com.rushcrew.product.presentation.dto.request.UpdateProductRequest;
import java.util.UUID;

public interface ProductService {

    CreateProductResult createProduct(CreateProductCommand command);
}
