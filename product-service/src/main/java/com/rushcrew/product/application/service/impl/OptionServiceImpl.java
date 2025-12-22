package com.rushcrew.product.application.service.impl;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.product.application.command.CreateOptionCommand;
import com.rushcrew.product.application.command.UpdateOptionCommand;
import com.rushcrew.product.application.result.UpdateOptionResult;
import com.rushcrew.product.application.service.OptionService;
import com.rushcrew.product.application.service.ProductPolicy;
import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.entity.ProductOption;
import com.rushcrew.product.domain.exception.ProductErrorCode;
import com.rushcrew.product.domain.model.UpdateOptionParams;
import com.rushcrew.product.domain.repository.ProductRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final ProductRepository productRepository;
    private final ProductPolicy productPolicy;

    @Override
    @Transactional
    public List<UUID> createProductOptions(
        Long userId, String role, UUID productId, List<CreateOptionCommand> commands
    ) {
        if (commands == null || commands.isEmpty()) {
            throw new BusinessException(ProductErrorCode.OPTION_LIST_EMPTY);
        }

        Product product = productPolicy.findAndValidateProduct(productId);
        productPolicy.validateSellerPermission(product, userId, role);

        List<ProductOption> newOptions = commands.stream()
            .map(command ->
                product.addOption(command.size(), command.color())).toList();
        productRepository.saveAndFlush(product);

        return newOptions.stream().map(ProductOption::getId).toList();
    }

    @Override
    @Transactional
    public UpdateOptionResult updateProductOption(
        Long userId, String role, UUID productId, UUID skuId, UpdateOptionCommand command
    ) {
        Product product = productPolicy.findAndValidateProduct(productId);
        productPolicy.validateSellerPermission(product, userId, role);

        ProductOption option = productRepository.findOptionBySkuId(skuId)
            .orElseThrow(() -> new BusinessException(ProductErrorCode.NOT_FOUND_OPTION));
        UpdateOptionParams params = new UpdateOptionParams(command.size(), command.color());

        option.update(params);

        return UpdateOptionResult.from(option);
    }

    @Override
    @Transactional
    public void deleteProductOption(Long userId, String role, UUID productId, UUID skuId) {
        Product product = productPolicy.findAndValidateProduct(productId);
        productPolicy.validateSellerPermission(product, userId, role);

        ProductOption option = productRepository.findOptionBySkuId(skuId)
            .orElseThrow(() -> new BusinessException(ProductErrorCode.NOT_FOUND_OPTION));
        option.softDelete(userId);
    }
}