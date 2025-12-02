package com.rushcrew.product.application.service.impl;

import com.rushcrew.product.application.command.CreateProductCommand;
import com.rushcrew.product.application.command.UpdateProductCommand;
import com.rushcrew.product.application.result.CreateProductResult;
import com.rushcrew.product.application.result.UpdateProductResult;
import com.rushcrew.product.application.service.ProductService;
import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.repository.ProductRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CreateProductResult createProduct(CreateProductCommand command) {
        // TODO: 요청한 사용자 userRole이 ADMIN or SELLEER인지 확인하는 로직 추가 예정

        Product product = Product.create(command);
        command.optionCommands().forEach(optionCommand ->
            product.addOption(optionCommand.size(), optionCommand.color())
        );

        Product newProduct = productRepository.save(product);
        return CreateProductResult.from(newProduct);
    }

    @Override
    @Transactional
    public UpdateProductResult updateProduct(UUID productId, UpdateProductCommand command) {
        Product product = findAndValidateProduct(productId);
        product.update(command);

        return UpdateProductResult.from(product);
    }

    // 유효성 검증한 product 반환
    private Product findAndValidateProduct(UUID productId) {
        // TODO: 요청한 사용자 userRole이 ADMIN or SELLEER인지 확인하는 로직 추가 예정

        return productRepository.findByIdAndDeletedAtIsNull(productId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
    }
}
