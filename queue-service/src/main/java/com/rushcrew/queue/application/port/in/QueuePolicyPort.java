package com.rushcrew.queue.application.port.in;

import com.rushcrew.queue.application.command.CreatePolicyCommand;
import com.rushcrew.queue.application.command.SearchPolicyCommand;
import com.rushcrew.queue.application.command.UpdatePolicyCommand;
import com.rushcrew.queue.application.dto.PageQuery;
import com.rushcrew.queue.application.dto.QueuePolicyQueryResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;

/**
 * application 계층 usecase 정의
 */
public interface QueuePolicyPort {
    QueuePolicyQueryResponse createQueuePolicy(CreatePolicyCommand command, Long userId, String role);

    Page<QueuePolicyQueryResponse> searchPolicies(PageQuery query, SearchPolicyCommand command, Long userId, String role);

    QueuePolicyQueryResponse getQueuePolicyInfo(UUID policyId, Long userId, String role);

    QueuePolicyQueryResponse updateQueuePolicy(UpdatePolicyCommand command, UUID policyId, Long userId, String role);

    void deleteQueuePolicy(UUID policyId, Long userId, String role);
}
