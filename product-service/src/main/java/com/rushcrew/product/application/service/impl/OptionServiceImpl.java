package com.rushcrew.product.application.service.impl;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.product.application.command.CreateOptionCommand;
import com.rushcrew.product.application.service.OptionService;
import com.rushcrew.product.application.service.ProductValidator;
import com.rushcrew.product.domain.entity.Product;
import com.rushcrew.product.domain.entity.ProductOption;
import com.rushcrew.product.domain.exception.ProductErrorCode;
import com.rushcrew.product.domain.repository.ProductRepository;
import java.util.ArrayList;
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

        List<ProductOption> newOptions = new ArrayList<>();
        commands.forEach(command -> {
            ProductOption newOption = product.addOption(command.size(), command.color());
            newOptions.add(newOption);
        });
        productRepository.flush();

        return newOptions.stream().map(ProductOption::getId).toList();
    }
}