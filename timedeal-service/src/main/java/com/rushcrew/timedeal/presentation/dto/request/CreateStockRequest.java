package com.rushcrew.timedeal.presentation.dto.request;

import com.rushcrew.timedeal.application.command.CreateStockCommand;
import java.util.UUID;

public record CreateStockRequest(
    UUID productId,
    Long totalStock
) {

    public CreateStockCommand toCommand() {
        return new CreateStockCommand(this.productId, this.totalStock);
    }
}
