package com.rushcrew.product.application.service.impl;

import com.rushcrew.product.application.command.CreateProductCommand;
import com.rushcrew.product.application.result.CreateProductResult;
import com.rushcrew.product.application.service.ProductService;
import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.repository.ProductRepository;
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
}
