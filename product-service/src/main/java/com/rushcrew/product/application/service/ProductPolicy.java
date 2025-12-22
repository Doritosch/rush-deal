package com.rushcrew.product.application.service;

import com.rushcrew.common.enums.UserRole;
import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.common.global.error.CommonErrorCode;
import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.exception.ProductErrorCode;
import com.rushcrew.product.domain.repository.ProductRepository;
import com.rushcrew.product.domain.vo.SellerId;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductPolicy {

    private final ProductRepository productRepository;

    /**
     * 유효성 검증한 product 반환
     */
    public Product findAndValidateProduct(UUID productId) {
        return productRepository.findByIdAndDeletedAtIsNull(productId)
            .orElseThrow(() -> new BusinessException(ProductErrorCode.NOT_FOUND_OPTION));
    }

    /**
     * 요청한 사용자의 USER_ROLE이 SELLER라면
     * 해당 product에 접근할 권한이 있는지 확인
     */
    public void validateSellerPermission(Product product, Long userId, String role) {
        if(role.equals(UserRole.SELLER.getDescription())
            && !Objects.equals(userId, product.getSellerId().getId())) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }

    /**
     * 요청한 사용자가 MASTER -> request의 sellerId 사용,
     *               SELLER -> @AuthenticationPrincipal로 가져온 userId 사용
     */
    public SellerId decideSellerId(Long sellerId, Long userId, String role) {
        if (role.equals(UserRole.MASTER.getDescription())) {
            if (sellerId == null) {
                throw new BusinessException(ProductErrorCode.REQUIRED_SELLER_ID);
            }
            return SellerId.of(sellerId);
        }

        if (role.equals(UserRole.SELLER.getDescription())) {
            return SellerId.of(userId);
        }

        throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }
}
