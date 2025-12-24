package com.rushcrew.timedeal.application.service;

import com.rushcrew.common.enums.UserRole;
import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.common.global.error.CommonErrorCode;
import com.rushcrew.timedeal.domain.entity.TimeDeal;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class TimeDealPolicy {

    /**
     * 요청한 사용자의 USER_ROLE이 SELLER라면 해당 TimeDeal에 접근할 권한이 있는지 확인
     */
    public void validateSellerPermission(TimeDeal timeDeal, Long userId, String role) {
        if (role.equals(UserRole.SELLER.getDescription())
            && !Objects.equals(userId, timeDeal.getTimeDealInfo().getSellerId())) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }
}
