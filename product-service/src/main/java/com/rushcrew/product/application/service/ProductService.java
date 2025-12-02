package com.rushcrew.product.application.service;

import com.rushcrew.product.application.command.CreateProductCommand;
import com.rushcrew.product.application.command.UpdateProductCommand;
import com.rushcrew.product.application.result.CreateProductResult;
import com.rushcrew.product.application.result.UpdateProductResult;
import java.util.UUID;

public interface ProductService {

    CreateProductResult createProduct(CreateProductCommand command);

    UpdateProductResult updateProduct(UUID productId, UpdateProductCommand command);
}
