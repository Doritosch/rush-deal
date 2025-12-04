package com.rushcrew.queue.presentation.controller;


import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.queue.application.command.queue.EnterQueueCommand;
import com.rushcrew.queue.application.dto.QueueRedisResponse;
import com.rushcrew.queue.application.port.in.QueuePort;
import com.rushcrew.queue.domain.enums.QueueStatus;
import com.rushcrew.queue.presentation.dto.response.QueueResponse;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/queues")
public class QueueController {

    private final QueuePort queuePort; // Service 대신 인터페이스 의존 (DIP)

    // API Gateway에서 인증 후, USER ID와 ROLE을 헤더에 담아 전달
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String QUEUE_TOKEN_HEADER = "X-Queue-Token";

    public QueueController(QueuePort queuePort) {
        this.queuePort = queuePort;
    }

    /**
     * 대기열 진입 요청 API
     */
    @PostMapping("/enter")
    public ResponseEntity<ApiResponse<QueueResponse>> enterQueue(
        @RequestParam UUID productId,
        @RequestHeader(USER_ID_HEADER) Long currUserId,
        @RequestHeader(USER_ROLE_HEADER) String role
    ) {
        EnterQueueCommand command = EnterQueueCommand.of(productId, currUserId, role);
        QueueRedisResponse redisResult = queuePort.enterQueue(command);
        QueueResponse response = toQueueResponse(redisResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 대기열 순번 조회(Polling) API
     */
    @GetMapping("/rank")
    public ResponseEntity<ApiResponse<QueueResponse>> getQueueRank(
        @RequestParam UUID productId,
        @RequestHeader(QUEUE_TOKEN_HEADER) String queueToken,
        @RequestHeader(USER_ID_HEADER) Long currUserId,
        @RequestHeader(USER_ROLE_HEADER) String role
    ) {
        QueueRedisResponse redisResult = queuePort.getQueueRank(productId, queueToken, currUserId, role);
        QueueResponse response = toQueueResponse(redisResult);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    // TODO: 추후 presenataion mapper 클래스로 이동 예정
    private QueueResponse toQueueResponse(QueueRedisResponse redisResponse) {
        String message = redisResponse.status().equals(QueueStatus.WAITING) ?
            "대기 중입니다." : "입장 가능합니다.";
        return QueueResponse.builder()
            .token(redisResponse.token())
            .productId(redisResponse.productId())
            .rank(redisResponse.rank())
            .status(redisResponse.status().getDescription())
            .enteredAt(redisResponse.enteredAt())
            .message(message)
            .build();
    }
}
