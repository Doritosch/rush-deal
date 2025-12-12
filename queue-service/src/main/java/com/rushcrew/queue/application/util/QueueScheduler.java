package com.rushcrew.queue.application.util;

import com.rushcrew.queue.application.port.in.QueuePort;
import com.rushcrew.queue.domain.entity.QueuePolicy;
import com.rushcrew.queue.domain.repository.QueuePolicyRepository;
import com.rushcrew.queue.domain.vo.TrafficSetting;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class QueueScheduler {
    private final QueuePolicyRepository queuePolicyRepository;
    private final QueuePort queueService;
    private final RedisTemplate<String, String> redisTemplate;

    // DB 부하를 줄이기 위한 인메모리 캐시 (스레드 세이프)
    private final List<QueuePolicy> cachedPolicies = new CopyOnWriteArrayList<>();

    // 마지막 실행 시간 기록 Redis 키
    private static final String LAST_RUN_KEY = "queue:scheduler:last_run:%s";

    public QueueScheduler(QueuePolicyRepository queuePolicyRepository, QueuePort queueService,
        RedisTemplate<String, String> redisTemplate) {
        this.queuePolicyRepository = queuePolicyRepository;
        this.queueService = queueService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * [Refresher] 정책 캐시 갱신 (주기: 1분)
     * 역할: DB에서 "현재 진행 중"이거나 "곧 시작(1분 내)할" 정책을 미리 가져옴
     * 목적: DB 조회 횟수를 획기적으로 줄임 (1분 1회)
     */
    @Scheduled(fixedRate = 60000)
    public void refreshPolicies() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteLater = now.plusMinutes(1);

        // 현재 유효한(진행 중인) 타임딜 정책 조회
        // TODO: RDB 부하가 걱정된다면 이 목록을 Redis에 캐싱
        List<QueuePolicy> allActivePolicies = queuePolicyRepository.findAllActivePolicies(now,
            oneMinuteLater);

        // 캐시 교체 (CopyOnWriteArrayList는 참조 교체 시 스레드 세이프)
        cachedPolicies.clear();
        cachedPolicies.addAll(allActivePolicies);
        log.info("[Scheduler:Refresher] 정책 캐시 갱신 완료. (로드된 정책 수: {})", allActivePolicies.size());
    }

    /**
     * 타임딜 활성화 스케줄러
     * 동적 스케줄러 (Ticker)
     * 1초(queueGap의 최소값) 주기로 실행하며 각 정책의 queueGap을 체크합니다.
     * 진행 중인 타임딜을 찾아 각자의 queueGap 주기에 맞춰 활성화를 요청함
     * DB I/O 없음 (위에 refreshPolicies에서 1분마다 캐시 갱신해줌)
     */
    @Scheduled(fixedDelay = 1000)
    public void scheduleActivation() {
        if (cachedPolicies.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (QueuePolicy policy : cachedPolicies) {
            // "곧 시작(1분 내)할" 정책도 refreshPolicies 메서드에서 미리 가져왔으므로,
            // 실제로 지금 시간이 시작 시간을 지났는지 메모리 상에서 체크
            if (!isWithinRunningTime(policy, now)) {
                continue;
            }
            processPolicy(policy);
        }

    }

    /**
     * 대기열 정책에서 대기열 진입 시간 확인 로직
     */
    private boolean isWithinRunningTime(QueuePolicy policy, LocalDateTime now) {
        LocalDateTime startTime = policy.getTimePeriod().getStartTime();
        LocalDateTime endTime = policy.getTimePeriod().getEndTime();
        return (now.isEqual(startTime) || now.isAfter(startTime)) && now.isBefore(endTime);
    }

    /**
     * 각 정책별로 활성화 로직 수행 (병렬 처리 가능)
     */
    private void processPolicy(QueuePolicy queuePolicy) {
        UUID productId = queuePolicy.getProductId();
        TrafficSetting setting = queuePolicy.getTrafficSetting();

        // 대기열 -> 활성열로 이동 주기
        int queueGap = setting.getQueueGap();

        // Redis에 기록된 마지막 실행 시간 체크 (실행 가능 여부 검사)
        if (canExecute(productId, queueGap)) {
            try {
                // 대기열 -> 활성열 이동 요청
                queueService.activateTokens(productId, setting);
                updateLastExecutionTime(productId);
            } catch (Exception e) {
                log.error("[Scheduler] 상품({}) 활성화 중 에러 발생", productId, e);
            }
        }
    }

    /**
     * 해당 상품의 스케줄러 실행 자격이 있는지 검사
     * 조건: (현재시간 - 마지막실행시간) >= queueGap
     */
    private boolean canExecute(UUID productId, int queueGap) {
        String lastRunKey = String.format(LAST_RUN_KEY, productId);
        String lastRunTimeStr = redisTemplate.opsForValue().get(lastRunKey);

        // 첫 시작에는 true
        if (lastRunTimeStr == null) return true;

        long lastRunTime = Long.parseLong(lastRunTimeStr);
        long currentTime = System.currentTimeMillis();
        long diffSeconds = (currentTime - lastRunTime) / 1000;

        return diffSeconds >= queueGap;
    }

    /**
     * 마지막 실행 시간 갱신
     */
    private void updateLastExecutionTime(UUID productId) {
        String lastRunKey = String.format(LAST_RUN_KEY, productId);
        redisTemplate.opsForValue().set(lastRunKey, String.valueOf(System.currentTimeMillis()));
    }
}
