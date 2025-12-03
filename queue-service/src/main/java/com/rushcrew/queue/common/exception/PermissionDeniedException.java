package com.rushcrew.queue.common.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.queue.common.QueueErrorCode;
import lombok.Getter;

@Getter
public class PermissionDeniedException extends BusinessException {

    public PermissionDeniedException(String message) {
        super(QueueErrorCode.FORBIDDEN_ACCESS);
    }
}