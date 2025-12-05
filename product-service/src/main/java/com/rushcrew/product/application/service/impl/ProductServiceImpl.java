package com.rushcrew.product.application.service.impl;

import com.rushcrew.product.application.ProductFilter;
import com.rushcrew.product.application.command.CreateProductCommand;
import com.rushcrew.product.application.command.UpdateProductCommand;
import com.rushcrew.product.application.result.CreateProductResult;
import com.rushcrew.product.application.result.ProductDetailResult;
import com.rushcrew.product.application.result.ProductOptionResult;
import com.rushcrew.product.application.result.ProductResult;
import com.rushcrew.product.application.result.UpdateProductResult;
import com.rushcrew.product.application.service.ProductService;
import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.model.CreateProductParams;
import com.rushcrew.product.domain.model.UpdateProductParams;
import com.rushcrew.product.domain.repository.ProductRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CreateProductResult createProduct(CreateProductCommand command) {
        // TODO: 요청한 사용자 userRole이 ADMIN or SELLEER인지 확인하는 로직 추가 예정

        CreateProductParams params = new CreateProductParams(
            command.sellerId(), command.companyName(), command.productInfo(),
            command.price(), command.category(), command.optionCommands()
        );

        Product product = Product.create(params);
        Product newProduct = productRepository.save(product);
        return CreateProductResult.from(newProduct);
    }

    @Override
    @Transactional
    public UpdateProductResult updateProduct(UUID productId, UpdateProductCommand command) {
        Product product = findAndValidateProduct(productId);
        checkPermission(product);

        UpdateProductParams params = new UpdateProductParams(
            command.companyName(), command.category(), command.price(),
            command.productName(), command.description()
        );
        product.update(params);

        return UpdateProductResult.from(product);
    }

    @Override
    @Transactional
    public void disableProduct(UUID productId) {
        Product product = findAndValidateProduct(productId);
        checkPermission(product);
        product.deactivate();
    }

    @Override
    @Transactional
    public void enableProduct(UUID productId) {
        Product product = findAndValidateProduct(productId);
        checkPermission(product);
        product.activate();
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productId) {
        Product product = findAndValidateProduct(productId);
        checkPermission(product);

        // TODO: 추후에 사용자 정보 가지고 오면 주석처리 풀 예정
//        product.delete(userId);
    }

    @Override
    public Page<ProductResult> getProducts(ProductFilter productFilter, Pageable pageable) {
        return productRepository.searchEnabledProducts(productFilter, pageable);
    }

    @Override
    public ProductDetailResult getProductDetail(UUID productId) {
        Product product = productRepository.findProductDetail(productId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        List<ProductOptionResult> optionResultList =
            product.getOptions().stream().map(ProductOptionResult::from).toList();

        return ProductDetailResult.of(product, optionResultList);
    }

    // 유효성 검증한 product 반환
    private Product findAndValidateProduct(UUID productId) {
        // TODO: 요청한 사용자 userRole이 ADMIN or SELLEER인지 확인하는 로직 추가 예정

        return productRepository.findByIdAndDeletedAtIsNull(productId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
    }

    private void checkPermission(Product product) {
        /* TODO: 요청한 사용자 userRole이
            ADMIN or product.sellerId와 일치하는 SELLEER인지
            확인하는 로직 추가 예정
        */
    }
}
