package com.rushcrew.product.application.service;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.exception.ProductErrorCode;
import com.rushcrew.product.domain.repository.ProductRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductValidator {

    private final ProductRepository productRepository;

    // 유효성 검증한 product 반환
    public Product findAndValidateProduct(UUID productId) {
        // TODO: 요청한 사용자 userRole이 ADMIN or SELLEER인지 확인하는 로직 추가 예정

        return productRepository.findByIdAndDeletedAtIsNull(productId)
            .orElseThrow(() -> new BusinessException(ProductErrorCode.NOT_FOUND_OPTION));
    }

    public void checkPermission(Product product) {
        /* TODO: 요청한 사용자 userRole이
            ADMIN or product.sellerId와 일치하는 SELLEER인지
            확인하는 로직 추가 예정
        */
    }
}
