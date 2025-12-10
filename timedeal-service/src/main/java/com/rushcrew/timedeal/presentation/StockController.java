package com.rushcrew.timedeal.presentation;

import com.rushcrew.timedeal.application.command.CreateStockCommand;
import com.rushcrew.timedeal.application.command.UpdateStockCountCommand;
import com.rushcrew.timedeal.application.result.CreateStockResult;
import com.rushcrew.timedeal.application.result.UpdateStockCountResult;
import com.rushcrew.timedeal.application.service.StockService;
import com.rushcrew.timedeal.presentation.dto.request.CreateStockRequest;
import com.rushcrew.timedeal.presentation.dto.request.UpdateStockCountRequest;
import com.rushcrew.timedeal.presentation.dto.response.CreateStockResponse;
import com.rushcrew.timedeal.presentation.dto.response.UpdateStockCountResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<CreateStockResponse> createStock(
        @RequestBody @Valid CreateStockRequest request
    ) {
        CreateStockCommand command = request.toCommand();
        CreateStockResult result = stockService.createStock(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateStockResponse.from(result));
    }

    @PostMapping("/{stockId}/change")
    public ResponseEntity<UpdateStockCountResponse> changeStockCount(
        @PathVariable UUID stockId,
        @RequestBody @Valid UpdateStockCountRequest request
    ) {
        UpdateStockCountCommand command = request.toCommand();
        UpdateStockCountResult result = stockService.changeStockCount(stockId, command);
        return ResponseEntity.ok(UpdateStockCountResponse.from(result));
    }

    @DeleteMapping("/{stockId}")
    public ResponseEntity<Void> deleteStock(
        @PathVariable UUID stockId
    ) {
        stockService.deleteStock(stockId);
        return ResponseEntity.noContent().build();
    }
}
