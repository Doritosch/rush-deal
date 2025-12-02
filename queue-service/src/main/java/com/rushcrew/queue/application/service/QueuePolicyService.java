package com.rushcrew.queue.application.service;

import com.rushcrew.queue.application.command.CreatePolicyCommand;
import com.rushcrew.queue.application.port.in.QueuePolicyPort;
import com.rushcrew.queue.domain.entity.QueuePolicy;
import com.rushcrew.queue.domain.repository.QueuePolicyRepository;
import com.rushcrew.queue.presentation.dto.response.CreatePolicyResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QueuePolicyService implements QueuePolicyPort {

    private final QueuePolicyRepository queuePolicyRepository;

    public QueuePolicyService(QueuePolicyRepository queuePolicyRepository) {
        this.queuePolicyRepository = queuePolicyRepository;
    }

    /**
     * 타임딜 정책 생성 : MASTER 권한만 가능
     */
    @Override
    @Transactional
    public CreatePolicyResponse createQueuePolicy(CreatePolicyCommand command, Long userId) {
        // TODO: 권한 유효성 검사

        // 중복 정책 검증 (해당 상품에 정책이 이미 있는지 검증)
        if (queuePolicyRepository.findByProductId(command.productId()).isPresent()) {
            throw new IllegalArgumentException("해당 상품에 대한 대기열 정책이 이미 존재합니다.");
        }

        QueuePolicy queuePolicy = QueuePolicy.create(
            command.productId(),
            command.dealName(),
            command.status(),
            command.timePeriod(),
            command.trafficSetting()
        );

        QueuePolicy saved = queuePolicyRepository.save(queuePolicy);
        return CreatePolicyResponse.from(saved);
    }
}
