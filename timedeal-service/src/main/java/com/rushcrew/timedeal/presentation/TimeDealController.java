package com.rushcrew.timedeal.presentation;

import com.rushcrew.timedeal.application.command.CreateTimeDealCommand;
import com.rushcrew.timedeal.application.command.UpdateTimeDealCommand;
import com.rushcrew.timedeal.application.result.UpdateTimeDealResult;
import com.rushcrew.timedeal.application.service.TimeDealService;
import com.rushcrew.timedeal.presentation.dto.request.CreateTimeDealRequest;
import com.rushcrew.timedeal.presentation.dto.request.UpdateTimeDealRequest;
import com.rushcrew.timedeal.presentation.dto.response.UpdateTimeDealResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/timedeals")
@RequiredArgsConstructor
public class TimeDealController {

    private final TimeDealService timeDealService;

    @PostMapping
    public ResponseEntity<UUID> createTimeDeal(
        @Valid @RequestBody CreateTimeDealRequest request
    ) {
        CreateTimeDealCommand command = request.toCommand();
        UUID timeDealId = timeDealService.createTimeDeal(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(timeDealId);
    }

    @PatchMapping("/{timeDealId}")
    public ResponseEntity<UpdateTimeDealResponse> updateTimeDeal(
        @Valid UpdateTimeDealRequest request,
        @PathVariable UUID timeDealId
    ) {
        UpdateTimeDealCommand command = request.toCommand();
        UpdateTimeDealResult result = timeDealService.updateTimeDeal(timeDealId, command);
        return ResponseEntity.ok(UpdateTimeDealResponse.from(result));
    }

    @PostMapping("/{timeDealId}/force-end")
    public ResponseEntity<Void> forceEndTimeDeal(
        @PathVariable UUID timeDealId
    ) {
        timeDealService.forceEndTimeDeal(timeDealId);
        return ResponseEntity.noContent().build();
    }
}
