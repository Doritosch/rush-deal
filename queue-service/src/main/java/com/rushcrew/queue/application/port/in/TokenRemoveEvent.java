package com.rushcrew.queue.application.port.in;

import java.util.UUID;

/**
 * 토큰 삭제 이벤트
 * Order Service와 Queue Service가 서로 주고받을 메시지 규격
 * @param productId
 * @param token
 * @param userId
 */
public record TokenRemoveEvent(
    UUID productId,
    String token,
    Long userId
) {

}
