package com.rushcrew.queue.presentation.controller;


import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.queue.application.command.queue.EnterQueueCommand;
import com.rushcrew.queue.application.dto.QueueRedisResponse;
import com.rushcrew.queue.application.port.in.QueuePort;
import com.rushcrew.queue.infrastructure.security.UserDetailsImpl;
import com.rushcrew.queue.presentation.dto.request.EnterQueueRequest;
import com.rushcrew.queue.presentation.dto.response.QueueResponse;
import com.rushcrew.queue.presentation.mapper.QueuePresentationMapper;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/queues")
public class QueueController {

    private final QueuePort queuePort; // Service 대신 인터페이스 의존 (DIP)
    private final QueuePresentationMapper queueMapper;

    private static final String QUEUE_TOKEN_HEADER = "X-Queue-Token";

    public QueueController(QueuePort queuePort, QueuePresentationMapper queueMapper) {
        this.queuePort = queuePort;
        this.queueMapper = queueMapper;
    }

    /**
     * 대기열 진입 요청 API
     */
    @PostMapping("/enter")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<QueueResponse>> enterQueue(
        @Valid @RequestBody EnterQueueRequest request,
        @AuthenticationPrincipal UserDetailsImpl principal
    ) {
        EnterQueueCommand command = EnterQueueCommand.of(request.productId(), principal.userId(),
            principal.role());
        QueueRedisResponse redisResult = queuePort.enterQueue(command);
        QueueResponse response = queueMapper.toQueueResponse(redisResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 대기열 순번 조회(Polling) API
     */
    @GetMapping("/rank")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<QueueResponse>> getQueueRank(
        @RequestParam UUID productId,
        @RequestHeader(QUEUE_TOKEN_HEADER) String queueToken,
        @AuthenticationPrincipal UserDetailsImpl principal
    ) {
        QueueRedisResponse redisResult = queuePort.getQueueRank(productId, queueToken, principal.userId());
        QueueResponse response = queueMapper.toQueueResponse(redisResult);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    /**
     * 대기열 취소/주문 완료 시 토큰 삭제 API
     * 사용자가 대기 중 "취소" 버튼을 누르거나, 주문 프로세스가 끝나고 나갈 때 호출
     */
    @DeleteMapping("/{product-id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteQueueToken(
        @PathVariable("product-id") UUID productId,
        @RequestHeader(QUEUE_TOKEN_HEADER) String queueToken,
        @AuthenticationPrincipal UserDetailsImpl principal
    ) {
        queuePort.exitQueue(productId, queueToken, principal.userId());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }
}
