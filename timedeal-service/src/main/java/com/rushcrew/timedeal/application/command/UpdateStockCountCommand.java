package com.rushcrew.timedeal.application.command;

import com.rushcrew.timedeal.domain.vo.Quantity;

public record UpdateStockCountCommand(
    Quantity quantity,
    String reason
) {

}
