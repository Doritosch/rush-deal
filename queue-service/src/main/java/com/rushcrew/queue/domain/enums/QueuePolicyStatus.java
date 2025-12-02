package com.rushcrew.queue.domain.enums;

import lombok.Getter;

/**
 * QueuePolicy가 담당하고 있는 타임딜 생명주기 관리
 * RUNNING : 정상 실행
 * PAUSED : 서버가 너무 느려지거나 결제 서비스가 터져서 잠시 입장을 막아야 할 때 스케줄러 동작 중지 (대기자는 계속 Wait 상태 유지)
 * STOPPED : 타임딜 시간이 끝났거나 재고 소진 시
 */
@Getter
public enum QueuePolicyStatus {
    RUNNING("타임딜 실행 중"), // 정상 작동 (대기열 진입 O, 활성큐 이동 O)
    PAUSED("타임딜 중지"), // 일시 중지 (대기열 진입 O, 활성큐 이동 X) -> 사고 났을 때 문 닫기
    STOPPED("타임딜 종료"); // 종료 (대기열 진입 X, 활성큐 이동 X) -> 타임딜 끝남

    private final String description;

    QueuePolicyStatus(String description) {
        this.description = description;
    }
}
