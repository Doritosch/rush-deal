package com.rushcrew.product.application.service;

import com.rushcrew.product.application.command.CreateOptionCommand;
import com.rushcrew.product.application.command.UpdateOptionCommand;
import com.rushcrew.product.application.result.UpdateOptionResult;
import java.util.List;
import java.util.UUID;

public interface OptionService {

    List<UUID> createProductOptions(
        Long userId, String role, UUID productId, List<CreateOptionCommand> commands);

    UpdateOptionResult updateProductOption(
        Long userId, String role, UUID productId, UUID skuId, UpdateOptionCommand command);

    void deleteProductOption(Long userId, String role, UUID productId, UUID skuId);
}
