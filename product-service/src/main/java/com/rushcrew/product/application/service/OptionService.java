package com.rushcrew.product.application.service;

import com.rushcrew.product.application.command.CreateOptionCommand;
import com.rushcrew.product.application.command.UpdateOptionCommand;
import com.rushcrew.product.application.result.UpdateOptionResult;
import java.util.List;
import java.util.UUID;

public interface OptionService {

    List<UUID> createProductOptions(UUID productId, List<CreateOptionCommand> commands);

    UpdateOptionResult updateProductOption(UUID productId, UUID skuId, UpdateOptionCommand command);

    void deleteProductOption(UUID productId, UUID skuId);
}
