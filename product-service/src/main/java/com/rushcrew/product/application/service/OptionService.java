package com.rushcrew.product.application.service;

import com.rushcrew.product.application.command.CreateOptionCommand;
import java.util.List;
import java.util.UUID;

public interface OptionService {

    List<UUID> createProductOptions(UUID productId, List<CreateOptionCommand> commands);

}
