package com.rushcrew.timedeal.application.command;

import com.rushcrew.timedeal.domain.vo.OrderId;
import com.rushcrew.timedeal.domain.vo.Quantity;
import java.util.UUID;

public record ConfirmStockCommand(
    OrderId orderId,
    UUID stockId,
    Quantity quantity
) {

}
