package com.rushcrew.product.presentation;

import com.rushcrew.product.application.command.CreateOptionCommand;
import com.rushcrew.product.application.command.UpdateOptionCommand;
import com.rushcrew.product.application.result.UpdateOptionResult;
import com.rushcrew.product.application.service.OptionService;
import com.rushcrew.product.presentation.dto.request.CreateOptionRequest;
import com.rushcrew.product.presentation.dto.request.UpdateOptionRequest;
import com.rushcrew.product.presentation.dto.response.UpdateOptionResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/{productId}")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;

    @PostMapping
    public ResponseEntity<List<UUID>> createProductOptions(
        @PathVariable UUID productId,
        @Valid @RequestBody List<CreateOptionRequest> requests
    ) {
        List<CreateOptionCommand> commands = CreateOptionCommand.fromList(requests);
        List<UUID> response = optionService.createProductOptions(productId, commands);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/options/{skuId}")
    public ResponseEntity<UpdateOptionResponse> updateProductOption(
        @PathVariable UUID productId,
        @PathVariable UUID skuId,
        @RequestBody UpdateOptionRequest request
    ) {
        UpdateOptionCommand command = UpdateOptionCommand.from(request);
        UpdateOptionResult result = optionService.updateProductOption(productId, skuId, command);
        return ResponseEntity.ok(UpdateOptionResponse.from(result));
    }

    @DeleteMapping("/options/{skuId}")
    public ResponseEntity<Void> DeleteProductOption(
        @PathVariable UUID productId,
        @PathVariable UUID skuId
    ) {
        optionService.deleteProductOption(productId, skuId);
        return ResponseEntity.noContent().build();
    }
}
