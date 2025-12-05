package com.rushcrew.product.application.service.impl;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.product.application.command.CreateOptionCommand;
import com.rushcrew.product.application.command.UpdateOptionCommand;
import com.rushcrew.product.application.result.UpdateOptionResult;
import com.rushcrew.product.application.service.OptionService;
import com.rushcrew.product.application.service.ProductValidator;
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
    private final ProductValidator productValidator;

    @Override
    @Transactional
    public List<UUID> createProductOptions(UUID productId, List<CreateOptionCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            throw new BusinessException(ProductErrorCode.OPTION_LIST_EMPTY);
        }

        Product product = productValidator.findAndValidateProduct(productId);
        productValidator.checkPermission(product);

        List<ProductOption> newOptions = commands.stream()
            .map(command ->
                product.addOption(command.size(), command.color())).toList();
        productRepository.saveAndFlush(product);

        return newOptions.stream().map(ProductOption::getId).toList();
    }

    @Override
    @Transactional
    public UpdateOptionResult updateProductOption(UUID productId, UUID skuId,
        UpdateOptionCommand command) {
        Product product = productValidator.findAndValidateProduct(productId);
        productValidator.checkPermission(product);

        ProductOption option = productRepository.findOptionBySkuId(skuId)
            .orElseThrow(() -> new BusinessException(ProductErrorCode.NOT_FOUND_OPTION));
        UpdateOptionParams params = new UpdateOptionParams(command.size(), command.color());

        option.update(params);

        return UpdateOptionResult.from(option);
    }

    @Override
    @Transactional
    public void deleteProductOption(UUID productId, UUID skuId) {
        Product product = productValidator.findAndValidateProduct(productId);
        productValidator.checkPermission(product);

        ProductOption option = productRepository.findOptionBySkuId(skuId)
            .orElseThrow(() -> new BusinessException(ProductErrorCode.NOT_FOUND_OPTION));
//        option.softDelete(userId); TODO: 추후에 주석처리 풀 예정
    }
}