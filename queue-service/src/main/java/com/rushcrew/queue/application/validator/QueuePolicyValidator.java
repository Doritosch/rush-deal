package com.rushcrew.queue.application.validator;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.queue.common.QueueErrorCode;
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
            throw new BusinessException(QueueErrorCode.ROLE_NOT_EXISTS);
        }

        if (userRole != UserRole.MASTER) {
            throw new BusinessException(QueueErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
