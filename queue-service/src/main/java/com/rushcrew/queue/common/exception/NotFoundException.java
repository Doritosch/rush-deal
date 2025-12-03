package com.rushcrew.queue.common.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.common.global.error.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
