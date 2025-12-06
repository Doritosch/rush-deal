package com.rushcrew.timedeal.presentation;

import com.rushcrew.timedeal.application.command.CreateTimeDealCommand;
import com.rushcrew.timedeal.application.service.TimeDealService;
import com.rushcrew.timedeal.presentation.dto.request.CreateTimeDealRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        CreateTimeDealCommand command = CreateTimeDealCommand.from(request);
        UUID timeDealId = timeDealService.createTimeDeal(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(timeDealId);
    }

    @PostMapping("/{timeDealId}/force-end")
    public ResponseEntity<Void> forceEndTimeDeal(
        @PathVariable UUID timeDealId
    ) {
        timeDealService.forceEndTimeDeal(timeDealId);
        return ResponseEntity.noContent().build();
    }
}
