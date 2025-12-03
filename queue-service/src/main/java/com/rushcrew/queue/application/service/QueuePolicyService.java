package com.rushcrew.queue.application.service;

import com.rushcrew.queue.application.command.CreatePolicyCommand;
import com.rushcrew.queue.application.command.SearchPolicyCommand;
import com.rushcrew.queue.application.command.UpdatePolicyCommand;
import com.rushcrew.queue.application.dto.PageQuery;
import com.rushcrew.queue.application.dto.QueuePolicyQueryResponse;
import com.rushcrew.queue.application.port.in.QueuePolicyPort;
import com.rushcrew.queue.domain.dto.SearchPolicyCondition;
import com.rushcrew.queue.domain.entity.QueuePolicy;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import com.rushcrew.queue.domain.repository.QueuePolicyRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public QueuePolicyQueryResponse createQueuePolicy(CreatePolicyCommand command, Long userId) {
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
        return QueuePolicyQueryResponse.from(saved);
    }

    /**
     * 타임딜 정책 목록 페이징 조회
     * Application DTO인 QueuePolicyQueryResponse의 Page를 반환
     */
    @Override
    @Transactional(readOnly = true)
    public Page<QueuePolicyQueryResponse> searchPolicies(PageQuery query, SearchPolicyCommand command,
        Long userId, String role) {
        QueuePolicyStatus status = command.getQueuePolicyStatus();
        SearchPolicyCondition condition = new SearchPolicyCondition(
            command.productId(),
            status
        );

        // TODO: 권한 허용 체크

        PageRequest pageable = query.toPageable();

        // specification 동적 쿼리 반환
        Page<QueuePolicy> policyPage = queuePolicyRepository.findAllByCondition(
            condition.productId(),
            condition.status(),
            pageable
        );

        // Page 변환하여 반환
        return policyPage.map(QueuePolicyQueryResponse::from);
    }

    /**
     * 특정 타임딜 정책 조회
     */
    @Override
    @Transactional(readOnly = true)
    public QueuePolicyQueryResponse getQueuePolicyInfo(UUID policyId, Long userId, String role) {
        // TODO: 권한 유효성 검사
        QueuePolicy queuePolicy = getQueuePolicy(policyId);
        return QueuePolicyQueryResponse.from(queuePolicy);
    }

    /**
     * 타임딜 정책 정보 수정
     * 권한 : 마스터(MASTER)
     */
    @Override
    @Transactional
    public QueuePolicyQueryResponse updateQueuePolicy(UpdatePolicyCommand command, UUID policyId, Long userId, String role) {
        // TODO: 권한 유효성 검사

        QueuePolicy queuePolicy = getQueuePolicy(policyId);
        queuePolicy.update(queuePolicy.getTimeDealName(),
            queuePolicy.getStatus(),
            queuePolicy.getTimePeriod(),
            queuePolicy.getTrafficSetting());
        return QueuePolicyQueryResponse.from(queuePolicy);
    }

    /**
     * 타임딜 정책 삭제 : MASTER 권한만 가능
     * */
    @Override
    @Transactional
    public void deleteQueuePolicy(UUID policyId, Long userId, String role) {
        QueuePolicy queuePolicy = getQueuePolicy(policyId);

        if (queuePolicy.isDeleted()) {
            throw new NoSuchElementException("이미 삭제된 정책 정보입니다.");
        }
        queuePolicy.softDelete(userId);
    }

    private QueuePolicy getQueuePolicy(UUID queuePolicyId) {
        return queuePolicyRepository.findById(queuePolicyId)
            .orElseThrow(() -> new NoSuchElementException("타임딜 정책 정보를 찾을 수 없습니다."));
    }

}
