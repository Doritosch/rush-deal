package com.rushcrew.timedeal.presentation.dto.request;

import com.rushcrew.timedeal.application.command.UpdateStockCountCommand;
import com.rushcrew.timedeal.domain.vo.Quantity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateStockCountRequest(

    @NotNull
    Long quantity,

    @NotBlank
    String reason
) {

    public UpdateStockCountCommand toCommand() {
        return new UpdateStockCountCommand(
            Quantity.of(this.quantity),
            this.reason
        );
    }
}
