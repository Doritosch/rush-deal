package com.rushcrew.queue.application.validator;

import com.rushcrew.queue.common.exception.PermissionDeniedException;
import com.rushcrew.queue.domain.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class QueuePolicyValidator {

    /**
     * MASTER 권한 검증
     */
    public void validateMasterRole(Long currUserId, String role) {
        // TODO : currUserId 관련 추가 검증 절차
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new PermissionDeniedException("유효하지 않은 권한입니다.");
        }

        if (userRole != UserRole.MASTER) {
            throw new PermissionDeniedException("해당 작업을 수행할 권한이 없습니다. (ROLE:" + role + ")");
        }
    }
}
