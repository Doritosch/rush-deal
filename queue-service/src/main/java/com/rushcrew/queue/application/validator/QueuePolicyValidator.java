package com.rushcrew.queue.application.validator;

import com.rushcrew.queue.common.exception.PermissionDeniedException;
import com.rushcrew.queue.domain.entity.QueuePolicy;
import com.rushcrew.queue.domain.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class QueuePolicyValidator {

    /**
     * 타임딜 정책 생성 권한 : MASTER
     */
    public void hasCreatePermission(Long currUserId, String role) {
        // TODO : currUserId 관련 추가 검증 절차
        UserRole userRole = UserRole.valueOf(role);

        if (userRole.equals(UserRole.MASTER)) {
            return;
        }
        throw new PermissionDeniedException("타임딜 정책을 생성할 권한이 없습니다. (ROLE:" + role + ")");
    }
}
