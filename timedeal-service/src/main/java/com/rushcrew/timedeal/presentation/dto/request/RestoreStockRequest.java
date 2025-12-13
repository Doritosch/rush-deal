package com.rushcrew.timedeal.presentation.dto.request;

import com.rushcrew.timedeal.application.command.RestoreStockCommand;
import com.rushcrew.timedeal.domain.vo.OrderId;
import com.rushcrew.timedeal.domain.vo.Quantity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RestoreStockRequest(
    @NotNull
    UUID timeDealStockId,

    @NotNull
    @Min(value = 1)
    Long quantity,

    @NotNull
    UUID orderId,

    @NotBlank
    String reason
) {

    public RestoreStockCommand toCommand() {
        return new RestoreStockCommand(
            this.timeDealStockId,
            Quantity.positive(this.quantity),
            OrderId.of(this.orderId),
            this.reason
        );
    }
}
